#!/bin/bash

title="Endless Nuclear Kittens"
applicationName="EndlessNuclearKittens.app"
finalDMGName="../build/dmg/EndlessNuclearKittens.dmg"
sourceFolder="../build/dmg/"
tmpDMGName="../build/pack.temp.dmg"

hdiutil create -scrub -srcfolder ${sourceFolder} -volname "${title}" -fs HFS+ -fsargs "-c c=64,a=16,e=16" -format UDRW -size `du -sm ${sourceFolder} | cut -f 1`m ${tmpDMGName}
device=$(hdiutil attach -readwrite -noverify -noautoopen "${tmpDMGName}" | egrep '^/dev/' | sed 1q | awk '{print $1}')

# Make the link + background folder
ln -s /Applications "/Volumes/${title}/"
mkdir "/Volumes/${title}/.background"
cp osx/background.png "/Volumes/${title}/.background/"

echo '
tell application "Finder"
    tell disk "'${title}'"
        open

        set current view of container window to icon view

        set icon size of icon view options of container window to 100
        set arrangement of icon view options of container window to not arranged

        set background picture of icon view options of container window to file ".background:background.png"
        set position of item "'${applicationName}'" to {100, 100}
        set position of item "Applications" to {340, 100}

        -- Swap the views so you can see the results
        set current view of container window to list view
        set current view of container window to icon view

        -- Some extra fudging else it forgets sizes
        set toolbar visible of container window to false
        set statusbar visible of container window to false
        set bounds of container window to {100, 100, 550, 300}
        set statusbar visible of container window to false
        close

        open
        update without registering applications
        delay 1

        set statusbar visible of container window to false
        set bounds of container window to {100, 100, 540, 290}
        update without registering applications

        delay 1
    end tell

    tell disk "'${title}'"
        set statusbar visible of container window to false
        set bounds of container window to {100, 100, 550, 300}

        update without registering applications

    end tell
    delay 3

    set waitTime to 0
    set ejectMe to false
    repeat while ejectMe is false
        delay 1
        if (do shell script "[ -f \"/Volumes/'${title}'/.DS_Store\" ]; echo $?") is "0" then set ejectMe to true
    end repeat
end tell
' | osascript

rm -rf /Volumes/"${title}"/.fseventsd
chmod -Rf go-w /Volumes/"${title}" > /dev/null || true
bless --folder /Volumes/"${title}" --openfolder /Volumes/"${title}"
sync
hdiutil detach ${device}
hdiutil convert ${tmpDMGName} -format UDZO -imagekey zlib-level=9 -o ${finalDMGName}
rm -f ${tmpDMGName}
