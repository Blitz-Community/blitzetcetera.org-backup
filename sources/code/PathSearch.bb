Const PATH_MAP_SIZEX = 100 
Const PATH_MAP_SIZEY = 100 
Const PATH_LIST_SIZE = 500 
 
Dim path_map%(2,PATH_MAP_SIZEX,PATH_MAP_SIZEY) 
Dim path_list%(1,1,PATH_LIST_SIZE) 
Dim path_listpos%(1) 
 
Global path_Currentlist% 
Global path_GetpointX% 
Global path_GetpointY% 
Global path_GetPos% 
 
Global path_ExitX% 
Global path_ExitY% 
Global path_StartX% 
Global path_StartY% 
 
Global path_NoElements% 
Global path_ToExit% 
Global path_Founded% 
 
Type path_point 
 Field x%,y% 
End Type 
 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;; FIND PATH main functions 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
 
Function path_find(sx%,sy%,ex%,ey%) 
 Delete Each path_point 
  
 path_CurrentList = 0 
 path_ExitX = ex 
 path_Exity = ey 
 path_StartX = sx 
 path_startY = sy 
   
 path_listpos(0) = 0 
 path_listpos(1) = 0 
 For x = 0 To 100 
 For y = 0 To 100 
 For i = 0 To 2 
  path_map(i,x,y) = 0 
 Next : Next :  Next 
  
 path_NoElements = False 
 path_ToExit = False 
 path_founded = False 
 
 path_addpoint(sx,sy) 
 path_updatecell(sx,sy,sx,sy) 
 path_swaplist()  
 Repeat 
  path_processpoints() 
  If (path_Founded) Or (path_ToExit) Then Exit 
  path_swaplist() 
  If (path_ToExit) Then Exit 
 Forever 
 If (path_Founded) path_compileresult() 
End Function 
 
Include "map.bb" 
Function path_cellvalid(x%,y%) 
 
 If (x<0) Or (y<0) Or (x>PATH_MAP_SIZEX) Or (y>PATH_MAP_SIZEY) Return False 
 If path_map(2,x,y) Return False 
 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
 ;;;; add your checks here !! 
 If x>14 Or y>14 Return False 
 If mymap(x,y) > -1 Return False 
 
 Return True 
End Function 
 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
 
Function path_stepx%( x%, dir%) 
 Select dir 
  Case 2,5,6 Return x+1 
  Case 4,7,8 Return x-1 
  Default Return x 
 End Select 
End Function 
 
Function path_stepy%( y%, dir%) 
 Select dir 
  Case 1,5,8 Return y+1 
  Case 3,6,7 Return y-1 
  Default Return y 
 End Select 
End Function 
 
Function path_addpoint( x%, y%) 
 path_list(path_currentlist,0, path_listpos(path_currentlist)) = x 
 path_list(path_currentlist,1, path_listpos(path_currentlist)) = y 
 path_listpos(path_currentlist) = path_listpos(path_currentlist) + 1 
 If path_listpos(path_currentlist) > PATH_LIST_SIZE path_toExit=True 
End Function 
 
Function path_getpoint() 
 Local l = Abs(path_currentlist-1) 
 If path_getpos = path_listpos(l) 
  path_NoElements = True 
  Return 
 End If 
 path_getpointx = path_list(l,0,path_getpos) 
 path_getpointy = path_list(l,1,path_getpos) 
 path_getpos = path_getpos + 1 
End Function 
 
Function path_swaplist() 
 path_NoElements=False 
 If path_listpos( path_currentlist) = 0 
  path_ToExit = True 
  Return 
 EndIf  
 path_currentlist = Abs(path_currentlist-1) 
 path_listpos(path_currentlist) = 0 
 path_getpos = 0 
End Function 
 
Function path_updatecell(x%,y%,fromx%,fromy%) 
 path_map(0,x,y) = fromx 
 path_map(1,x,y) = fromy 
 path_map(2,x,y) = True 
 If (x = path_ExitX) And (y = path_ExitY) 
  path_founded = True 
  path_Toexit = True 
 End If 
End Function 
 
Function path_processpoints() 
 Local dir,x,y 
 Repeat 
  If path_ToExit Then Exit 
  path_getpoint() 
  If path_NoElements Then Exit 
  For dir = 1 To 8 
   x = path_Stepx( path_GetPointX, dir) 
   y = path_StepY( path_GetPointY, dir) 
   If path_cellvalid(x,y) 
    path_addpoint(x,y) 
    path_updatecell(x,y,path_GetPointX,path_GetPointY) 
    If path_ToExit Then Exit 
   End If 
  Next 
 Forever 
End Function 
 
Function path_compileresult() 
 Local x = path_Exitx 
 Local y = path_Exity 
 Local xx,yy 
 
 Repeat 
  If x = path_StartX And y = path_StartY 
   Exit 
  End If 
  p.path_point = New path_point 
  p\x = x 
  p\y = y 
 
  xx = path_map(0,x,y) 
  yy = path_map(1,x,y) 
 
  x = xx 
  y = yy 
 Forever 
End Function