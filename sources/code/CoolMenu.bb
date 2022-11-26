;==============Menu function==================== 
;==by [WC]Killer================================ 
;==============www.pwekiller.narod.ru================= 
;==============www.combatsoft.web1000.com============ 
;=======Free for all========== 
 
Dim MenuItem$(40) 
Dim MenuGroup%(40) 
Dim ChildGroup%(40) 
Dim ItemValue%(40) 
Dim KeyName$(40) 
Dim ItemValueMax%(40) 
Dim ItemValueMin%(40) 
Dim keyValue%(40) 
;===item name=grup index=child group index=any value or not(-1)=min =max=key value (scan code) 
 
.menudata 
Data "NEW GAME",1,2,-1,0,0,0 
Data "RESUME", 1,0,-1,0,0,0 
Data "SAVE",  1,0,-1,0,0,0 
Data "LOAD",  1,0,-1,0,0,0 
Data "VIDEO",  1,3,-1,0,0,0 
Data "AUDIO", 1,5,-1,0,0,0 
Data "CONTROLS",1,4,-1,0,0,0 
Data "EXIT",  1,0,-1,0,0,0 
 
Data "ESY",  2,0,-1,0,0,0 
Data "MEDIUM", 2,0,-1,0,0,0 
Data "HARD",  2,0,-1,0,0,0 
;===video switch flags=== 
Data "640_480, 16 BIT",3,0,-3,0,0,0 
Data "640_480, 32 BIT",3,0,-4,0,0,0 
Data "800_600, 16 BIT",3,0,-5,0,0,0 
Data "800_600, 32 BIT",3,0,-6,0,0,0 
Data "1024_960, 16 BIT",3,0,-7,0,0,0 
Data "1024_960, 32 BIT",3,0,-8,0,0,0 
 
Data "UP",  4,0,-2,0,0,0 
Data "DOWN", 4,0,-2,0,0,0 
Data "LEFT",  4,0,-2,0,0,0 
Data "RIGHT",  4,0,-2,0,0,0 
Data "HIGH FOOT",4,0,-2,0,0,0 
Data "LOW FOOT",4,0,-2,0,0,0 
Data "HIGHT HAND",4,0,-2,0,0,0 
Data "LOW HAND",4,0,-2,0,0,0 
Data "BLOCK", 4,0,-2,0,0,0 
Data "FLIP",  4,0,-2,0,0,0 
 
 
Data "MUSIK VOLUME",5,0,6,0,10,0 
Data "SOUND VOLUME",5,0,6,0,10,0 
 
Global P_KEY_NAME$ 
Global P_KEY_CODE% 
Global VID_COLORS% 
Global VID_HEIGHT% 
Global VID_WIDTH% 
Global S_VOLUME% 
Global M_VOLUME% 
 
 
Global MenuGroupO%=1 
Global MenuGroupMax%=8 
Global MenuGroupMin%=1 
Global LastMenuGroup%=1 
Global MenuItemCount%=29 ; must be change if menu data change 
 
 
Restore menudata 
For i=1 To MenuItemCount% 
 Read  MenuItem$(i), MenuGroup%(i),  ChildGroup%(i), ItemValue%(i) , ItemValueMin%(i) ,ItemValueMax%(i) ,KeyValue%(i) 
Next 
 
 
;====================Main function===================== 
 
Graphics 640,480,16,0 
;Warning! if you change font size, programm automaticaly adapted all to new font size 
;dont worry about it 
Global  menufont=LoadFont("Arial",24,True,False,False) 
SetFont(menufont) 
Color 0,255,0 
 
 
; for use in you game uncomment it and call in you programm 
; function MenuShow() 
 
IND%=1 
Repeat 
 
 
DrawMenu(MenuGroupO%,ind) 
;--Navigation------------------------------- 
If KeyHit( 200 )  Then IND=IND-1 : flag=1  :  PlaySound( MENU_SND) 
If KeyHit( 208 )  Then IND=IND+1 : flag=1  : PlaySound( MENU_SND) 
;-----------Change value if it enabled 
If ItemValue(IND)>=0 Then  
  If KeyHit( 205 )  Then ItemValue(IND)=ItemValue(IND)+1  
  If KeyHit( 203 )  Then ItemValue(IND)=ItemValue(IND)-1  
  If ItemValue(IND)<ItemValueMin(ind) Then ItemValue(IND)=ItemValueMin(ind) 
  If itemValue(IND)>ItemValueMax(ind) Then ItemValue(IND)=ItemValueMax(ind) 
EndIf 
 
 
 
;`-------Use menu item------------------------- 
If KeyHit(28) 
 
 
 
;===menu item 8 - exit 
If  ind=8 Then Exit 
 
 
DrawMenu(MenuGroupO%,ind) 
 
;====Detup key?======================== 
If  itemValue%(ind)=-2 Then  
 Text 30,30,"Press key" 
 While KeyDown(28)
 Wend 
 FlushKeys 
 WaitKey 
 If GetScanCode()=True Then 
  KeyName$(ind)=P_KEY_NAME$ 
  ItemValuemax(ind)=P_KEY_CODE% 
 EndIf 
EndIf 
If  ChildGroup%(IND)>0 Then  
  LastMenuGroup%=MenuGroupO% 
  MenuGroupO%=ChildGroup%(IND) 
EndIf 
 
Select itemValue%(ind) 
Case -3 
Mode3dChange(640, 480, 16) 
Case -4 
Mode3dChange( 640,480,32) 
Case -5 
Mode3dChange(800,600,16) 
Case -6 
Mode3dChange(800,600,32) 
Case -7 
Mode3dChange( 1024,768,16) 
Case -8 
Mode3dChange( 1024,768,32) 
End Select 
 
EndIf 
 
;`------------Return prevois menu----------------------------- 
If KeyHit(1)  Then MenuGroupO%=LastMenuGroup% 
 
If IND<MenuGroupMin% Then ind=MenuGroupMin%  
If IND>MenuGroupMax% Then ind=MenuGroupMax% 
 
Flip 
Forever 
 
 
 
; for use in you game uncomment it and call in you programm 
;End Function  
	
Function DrawMenu(mgrup, ind) 
w=GraphicsWidth()/2 
Cls 
k=10 
Stepf%=FontHeight() 
 
 
MenuGroupMin%=-1 
MenuGroupMax%=-1 
 
For I=1 To MenuItemCount% 
If MenuGroup(i)=mgrup Then 
 If  MenuGroupMin%=-1 Then MenuGroupMin%=i 
  MenuGroupmax%=i 
  Lne$=MenuItem$(I) 
  If ItemValue(i)>=0 Then Lne$=lne$+" "+ItemValue(i) 
  If ItemValue(i)=-2 Then Lne$=lne$+" "+KeyName$(I) 
 
  If i=ind Then 
   stw%=StringWidth(Lne$) 
  h%=StringHeight (Lne$) 
    Rect  w-stw/2,k,stw,h,False 
  EndIf 
   
  Text w,k,Lne$,True 
  k=k+Stepf% 
 EndIf 
 
Next 
End Function 
;End 
 
 
Function Mode3dChange(wid,heig,bpp) 
w=GraphicsWidth()/2 
h=GraphicsHeight()/2 
FlushKeys() 
Cls 
If  GfxMode3DExists (wid, heig, bpp)  
 VID_COLORS=bpp 
 VID_HEIGHT=heig 
 VID_WIDTH=wid 
 Text w,h,"VIDEO MODE WILL BE APPLIED ON NEXT START",True 
Else 
 Text w,h,"VIDEO MODE NOT SUPPORT ON YOU f**kING VIDEO SUCK SISTEM!",True 
EndIf 
;Flip 
WaitKey() 
FlushKeys() 
End Function 
 
 
 
 
Function GetScanCode() 
 Restore keydata 
 P_KEY_CODE%=0 
 Repeat 
  Read P_KEY_NAME$, P_KEY_CODE% 
  If KeyDown(P_KEY_CODE) Then  Return True 
  Until P_KEY_CODE%=0 
 Return False 
End Function 
 
.keydata 
Data "KEY_1",2  
Data "KEY_2",3  
Data "KEY_3",4  
Data "KEY_4",5  
Data "KEY_5",6  
Data "KEY_6",7  
Data "KEY_7",8  
Data "KEY_8",9  
Data "KEY_9",10  
Data "KEY_0",11  
Data "KEY_MINUS",12  
Data "KEY_EQUALS",13  
Data "KEY_BACKSPACE",14  
Data "KEY_TAB",15  
Data "KEY_Q",16  
Data "KEY_W",17  
Data "KEY_E",18  
Data "KEY_R",19  
Data "KEY_T",20  
Data "KEY_Y",21  
Data "KEY_U",22  
Data "KEY_I",23  
Data "KEY_O",24  
Data "KEY_P",25  
Data "KEY_LEFT_BRACKET",26  
Data "KEY_RIGHT_BRACKET",27  
Data "KEY_RETURN",28  
Data "KEY_LEFT_CONTROL",29  
Data "KEY_A",30  
Data "KEY_S",31  
Data "KEY_D",32  
Data "KEY_F",33  
Data "KEY_G",34  
Data "KEY_H",35  
Data "KEY_J",36  
Data "KEY_K",37  
Data "KEY_L",38  
Data "KEY_SEMICOLON",39  
Data "KEY_APOSTROPHE",40  
Data "KEY_GRAVE",41  
Data "KEY_LEFT_SHIFT",42  
Data "KEY_BACKSLASH",43  
Data "KEY_Z",44  
Data "KEY_X",45  
Data "KEY_C",46  
Data "KEY_V",47  
Data "KEY_B",48  
Data "KEY_N",49  
Data "KEY_M",50  
Data "KEY_COMMA",51  
Data "KEY_PERIOD",52  
Data "KEY_SLASH",53  
Data "KEY_RIGHT_SHIFT",54  
Data "KEY_MULTIPLY",55  
Data "KEY_LEFT_ALT",56  
Data "KEY_SPACE",57  
Data "KEY_CAPITAL",58  
Data "KEY_F1",59  
Data "KEY_F2",60  
Data "KEY_F3",61  
Data "KEY_F4",62  
Data "KEY_F5",63  
Data "KEY_F6",64  
Data "KEY_F7",65  
Data "KEY_F8",66  
Data "KEY_F9",67  
Data "KEY_F10",68  
Data "KEY_NUMLOCK",69  
Data "KEY_SCROLLLOCK",70  
Data "KEY_NUMPAD7",71  
Data "KEY_NUMPAD8",72  
Data "KEY_NUMPAD9",73  
Data "KEY_SUBTRACT",74  
Data "KEY_NUMPAD4",75  
Data "KEY_NUMPAD5",76  
Data "KEY_NUMPAD6",77  
Data "KEY_ADD",78  
Data "KEY_NUMPAD1",79  
Data "KEY_NUMPAD2",80  
Data "KEY_NUMPAD3",81  
Data "KEY_NUMPAD0",82  
Data "KEY_DECIMAL",83  
Data "KEY_OEM_102",86  
Data "KEY_F11",87  
Data "KEY_F12",88  
Data "KEY_F13",100  
Data "KEY_F14",101  
Data "KEY_F15",102  
Data "KEY_KANA",112  
Data "KEY_ABNT_C1",115  
Data "KEY_CONVERT",121  
Data "KEY_NOCONVERT",123  
Data "KEY_YEN",125  
Data "KEY_ABNT_C2",126  
Data "KEY_NUMPAD_EQUALS",141  
Data "KEY_PREVTRACK",144  
Data "KEY_AT",145  
Data "KEY_COLON",146  
Data "KEY_UNDERLINE",147  
Data "KEY_KANJI",148  
Data "KEY_STOP",149  
Data "KEY_AX",150  
Data "KEY_UNLABELED",151  
Data "KEY_NEXTTRACK",153  
Data "KEY_ENTER",156  
Data "KEY_RIGHT_CONTROL",157  
Data "KEY_MUTE",160  
Data "KEY_CALCULATOR",161  
Data "KEY_PLAY_PAUSE",162  
Data "KEY_MEDIASTOP",164  
Data "KEY_VOLUME_DOWN",174  
Data "KEY_VOLUME_UP",176  
Data "KEY_WEB_HOME",178  
Data "KEY_NUMPAD_COMMA",179  
Data "KEY_DIVIDE",181  
Data "KEY_SYSREQ",183  
Data "KEY_RIGHT_ALT",184  
Data "KEY_PAUSE",197  
Data "KEY_HOME",199  
Data "KEY_UP",200  
Data "KEY_PAGEUP",201  
Data "KEY_LEFT",203  
Data "KEY_RIGHT",205  
Data "KEY_END",207  
Data "KEY_DOWN",208  
Data "KEY_NEXT",209  
Data "KEY_INSERT",210  
Data "KEY_DELETE",211  
Data "KEY_LEFTWINDOWS",219  
Data "KEY_RIGHTWINDOWS",220  
Data "KEY_APPS",221  
Data "KEY_POWER",222  
Data "KEY_SLEEP",223  
Data "KEY_WAKE",227  
Data "KEY_WEBSEARCH",229  
Data "KEY_WEBFAVORITES",230  
Data "KEY_WEBREFRESH",231  
Data "KEY_WEBSTOP",232  
Data "KEY_WEBFORWARD",233  
Data "KEY_WEBBACK",234  
Data "KEY_MYCOMPUTER",235  
Data "KEY_MAIL",236  
Data "KEY_MEDIASELECT",237  
Data "KEY_MEDIASELECT ", 237  
Data "END",0

