Type Dlg 
 Field Img 
 Field Sel# 
 Field ImgS 
 Field ImgNS 
 Field ID# 
 Field Key# 
 Field X# 
 Field Y# 
End Type  
 
Graphics3D 640,480 
SetBuffer BackBuffer() 
 
Global KeyDlg#=-1 
GGG$="Super-Man" 
gold#=1000 
 
Global fnt=LoadFont("Arial",16,False,False,False) 
Global fntB=LoadFont("Arial",16,True,False,False) 
Global fntBI=LoadFont("Arial",16,True,True,False) 
Global fntI=LoadFont("Arial",16,False,True,False) 
 
tx$=VarInString$("Player name=[p1] and count gold=[p2]",GGG,gold) 
 
DlgString("- This sword by cost [B][S]1.000[s][b] gold, hero. Good weapon, his name [B]Great Sword[b]... [I][C]([D]Patric[I] open the chest)[c][i]", 10, 180,855,1) 
DlgString("- You want bay this bow? [B]Wood Bow[b] cost [B][S]800[s][b] gold, hero, please  [I][C]([D]Patric[I] look for you)[c][i]", 10, 200,805,2) 
DlgString("- Im closed, go to another shop, hero. Shop [B][S]Gold Sword[s][b] - its work [B]every time[b]. [I][C]([D]Patric[I] closed the door)[c][i]", 10, 220,3558,3) 
 
; set the font and print text 
 
While Not KeyDown( 1 ) 
      RenderWorld 
      ClsColor 0,0,0 
      Cls 
      DrawDlg() 
      If KeyHit(2) Then KeyDlg=1 
      If KeyHit(3) Then KeyDlg=2 
      If KeyHit(4) Then KeyDlg=3 
        Text 10,10,KeyDlg         
setmycolor(255255255) 
Text 10,50,tx$ 
 
      Flip 
Wend 
 
Function SetMyColor(Col) 
Local R,G,B 
 R=Substr(Str(Int(Col)),1,3) 
 G=Substr(Str(Int(Col)),4,3) 
 B=Substr(Str(Int(Col)),7,3) 
Color R,G,B 
End Function 
 
End 
 
Function Substr$(Txt$,StartPos#,Num#=0) 
Local TxtN$ 
TxtN=Right(Txt$, Len(txt$)-StartPos#+1)  
If Num#=0 Then Num#=Len(TxtN$) 
TxtN$=Left(TxtN$,Num#) 
 Return TxtN$ 
End Function 
 
Function DlgString(Txt$,X#,Y#,ID,key$) 
Local i#,j#,k#,l# 
Local C$,S$,T$ 
Local ColC#=200200200 
Local  ColS#=255255255 
Local  Col#=  255255000 
;[B][b] - жирный  
;[I][i]   - курсив  
;[D][d] - жирный курсив 
;[C][c] - комментарий 
;[S][s] - выделенный 
Img=CreateImage(StringWidth(Txt)+6,StringHeight(Txt)+8) 
SetBuffer ImageBuffer(Img) 
ClsColor 50,50,200 
Cls 
SetMyColor(Col) 
SetFont fnt 
 
K=3 
i=1 
While  i<Len(txt)+1 
C=substr(txt,i,1)  
If C="[" Then  
S=substr(txt,i,3) Select S 
 Case "[B]" 
  SetFont fntB 
 Case "[b]","[i]","[d]"  
  SetFont fnt 
 Case "[I]" 
  SetFont fntI 
 Case "[D]" 
  SetFont fntBI 
 Case "[c]","[s]" 
  SetMyColor(Col) 
 Case "[C]" 
  SetMyColor(ColC) 
 Case "[S]" 
  SetMyColor(ColS) 
End Select  
i=i+2 
Else  
  Text k,2,c 
  K=K+StringWidth(C) 
EndIf 
i=i+1 
Wend 
 
SetBuffer BackBuffer() 
temp=CopyImage(img)  
SetBuffer ImageBuffer(temp)  
setmycolor(col) 
Rect 0,0,k+3,StringHeight(Txt)+4,0 
SetBuffer BackBuffer() 
 
MaskImage img,50,50,200 
MaskImage temp,50,50,200 
 
;DrawImage temp,x,y  
 
NewDlg.Dlg=New Dlg 
NewDlg\Img=Img 
NewDlg\ImgNS=Img 
NewDlg\ImgS=temp 
NewDlg\ID#=ID 
NewDlg\key$=key$;*(key=0)+key*(key>0) 
NewDlg\X=X 
NewDlg\Y=Y 
End Function 
 
Function DrawDlg() 
 For NDlg.Dlg = Each Dlg 
;  If Int(KeyDlg)+1=NDlg\key Then 
  If KeyDlg=NDlg\key Then 
   NDlg\Img=NDlg\ImgS 
   Text 50,10,"select string ID="+Int(NDlg\ID)+" key="+Int(NDlg\key) 
   Else     
   NDlg\Img=NDlg\ImgNS 
  EndIf  
  
  DrawImage NDlg\Img,NDlg\X,NDlg\Y 
   
 Next 
End Function  
 
Function VarInString$(Txt$,p1$=0,p2$=0,p3$=0,p4$=0,p5$=0,p6$=0,p7$=0,p8$=0,p9$=0, p10$=0) 
Local i#,j#,k#,l# 
Local C$,S$,T$ 
 T$=Txt$ 
 T$=Replace(T$, "[p1]", Str(p1))  
 T$=Replace(T$, "[p2]", Str(p2))  
 T$=Replace(T$, "[p3]", Str(p3))  
 T$=Replace(T$, "[p4]", Str(p4))  
 T$=Replace(T$, "[p5]", Str(p5))  
 T$=Replace(T$, "[p6]", Str(p6))  
 T$=Replace(T$, "[p7]", Str(p7))  
 T$=Replace(T$, "[p8]", Str(p8))  
 T$=Replace(T$, "[p9]", Str(p9))  
 T$=Replace(T$, "[p10]", Str(p10))  
 
Return T$ 
End Function