; Wotlas Client config file For Nullsoft Scriptable Install System
;
; This script provides uninstall support and (optionally) start menu shortcuts.
;
; It will create a wotlas-client-1.3-beta.exe in the lib/ directory
;

; The name of the installer
Name "Wotlas Client 1.3 beta"
Icon "wotlas.ico"
Caption "Wotlas Client 1.3 beta"
WindowIcon on

BGGradient 000000 800000 FFFFFF
InstallColors FF8080 000030

; The file to write
OutFile "wotlas-client-1.3-beta.exe"

LicenseText "Wotlas is available under the GNU Public License. You'll find the source code of this program on SourceForge.net (http://sf.net/projects/wotlas)."
LicenseData "gpl.txt"

; The default installation directory
InstallDir $PROGRAMFILES\WotlasClient
; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM SOFTWARE\NSIS_WOTLAS_CLIENT "Install_Dir"

; The text to prompt the user to enter a directory
ComponentText "This will install Wheel Of Time - Light & Shadow on your computer. Select what you want to install."
; The text to prompt the user to enter a directory
DirText "Choose a directory where to install Wotlas:"

; The stuff to install
Section "Wotlas Client (required)"
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  ; Put file there
  File "wotlas-client.jar"
  File "wotlas.ico"
  File "wotlas.url"
  File /r "help"
  ; Write the installation path into the registry
  WriteRegStr HKCR "WOTLASFile\DefaultIcon" "" $INSTDIR\wotlas-client.jar,0
  WriteRegStr HKLM SOFTWARE\NSIS_WOTLAS_CLIENT "Install_Dir" "$INSTDIR"
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\WotlasClient" "DisplayName" "Wotlas Client (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\WotlasClient" "UninstallString" '"$INSTDIR\uninstall-client.exe"'
  WriteUninstaller "uninstall-client.exe"
SectionEnd

; optional section
Section "Start Menu Shortcuts"
  CreateDirectory "$SMPROGRAMS\Wotlas"
  CreateShortCut "$SMPROGRAMS\Wotlas\Wotlas Client.lnk" "$INSTDIR\wotlas-client.jar" "" "$INSTDIR\wotlas.ico"
  CreateShortCut "$SMPROGRAMS\Wotlas\help.lnk" "$INSTDIR\help\release-client.html" ""
  CreateShortCut "$SMPROGRAMS\Wotlas\wotlas.lnk" "$INSTDIR\wotlas.url" ""
  CreateShortCut "$SMPROGRAMS\Wotlas\Uninstall-Client.lnk" "$INSTDIR\uninstall-client.exe" "" "$INSTDIR\uninstall-client.exe" 0  
SectionEnd

; optional section Desktop Shortcut
Section "Desktop Shortcut"
  CreateShortCut "$DESKTOP\Wotlas Client.lnk" "$INSTDIR\wotlas-client.jar" "" "$INSTDIR\wotlas.ico"
SectionEnd


; uninstall stuff

UninstallText "This will uninstall your Wotlas Client. Hit next to continue."

; special uninstall section.
Section "Uninstall"
  ; remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\WotlasClient"
  DeleteRegKey HKLM SOFTWARE\NSIS_WOTLAS_CLIENT
  DeleteRegKey HKCR "WOTLASFile\DefaultIcon"

  ; remove files
  Delete $INSTDIR\wotlas-client.jar
  Delete $INSTDIR\wotlas.ico
  Delete $INSTDIR\wotlas.url
  RMDir /r $INSTDIR\help

  ; MUST REMOVE UNINSTALLER, too
  Delete $INSTDIR\uninstall-client.exe

  ; remove shortcuts, if any.
  Delete "$SMPROGRAMS\Wotlas\Wotlas Client.lnk"
  Delete "$SMPROGRAMS\Wotlas\help.lnk"
  Delete "$SMPROGRAMS\Wotlas\wotlas.url"
  Delete "$SMPROGRAMS\Wotlas\Uninstall-Client.lnk"  
  Delete "$DESKTOP\Wotlas Client.lnk"

  ; remove directories used.
  RMDir "$SMPROGRAMS\Wotlas"
  RMDir "$INSTDIR"
SectionEnd

Function un.onUninstSuccess
    IfFileExists $INSTDIR\base-ext\*.* lbl_found    
    Goto lbl_end
    lbl_found:
    MessageBox MB_OK "Your character data were not deleted. If you want to do so, you must manually $\ndelete the directory $INSTDIR\base-ext"
    lbl_end:
FunctionEnd


; eof
