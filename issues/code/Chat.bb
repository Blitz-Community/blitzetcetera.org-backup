<code bb>
;**********************************************
;ЧАТ                                          *
;АВТОР: TankSoft           *
;МЫЛО : TankSoft@rambler.ru                   *
;САЙТ : http://TankSoft.narod.ru              *
;**********************************************
Global chat$=""  ;Строка чата
Global name$="noname";Имя чувака

Type Player
 Field name$,net_id
End Type

Type Info
 Field txt$
End Type

;===================================
;Стартуем сетевую гаму
newGame = StartNetGame()
If newGame = 0 Then
 End;не получилось
EndIf
;===================================

;===================================
;Узнаём имя
.label1:Cls
name$=Input$( "Ваше имя? " )
If name$="" Then Goto label1
;===================================

;===================================
;Локальный плеер
Global player.Player=New Player
player\name=name$
player\net_id=CreateNetPlayer( name$ )
myid=player\net_id
;===================================

Cls
While Not KeyHit(1)
 Cls
 UpdateNetwork()
 UpdatePlayers()
 
 RenderAll()
 
 Flip
Wend

Function UpdateNetwork()
 While RecvNetMsg()
  Select NetMsgType()
  Case 2:
   ;Сообщение принято!!
   info( NetPlayerName$( NetMsgFrom() )+": "+NetMsgData$() )
  Case 100:
   ;новый чувак влез к нам!!...
   p.Player=New Player
   p\net_id=NetMsgFrom()
   p\name=NetPlayerName$( NetMsgFrom() )
   info( "Входит "+p\name )
  Case 101:
   ;...и ушел
   p.Player=FindPlayer( NetMsgFrom() )
   If p<>Null
    info( "Вышел "+p\name )
    Delete p
   EndIf
  Case 102:
   ;сервак закрылся
   info( "Теперь я сервер" )
  Case 200:
   ;Must Die сети
   EndGraphics
   Print "Сессия прервана"
   WaitKey
   End
  End Select
 Wend
End Function

Function UpdatePlayers()
 For p.Player=Each Player
  If NetPlayerLocal( p\net_id )
   ;Если гамер локальный, то набираем текст
   key=GetKey()
   If key
    If key=13
     ;13=ENTER ОТСЫЛАЕМ ВСЕМ СВОЙ БАЗАР
     If chat$<>"" Then SendNetMsg 2,chat$,p\net_id,0,0:info(name$+": "+chat$)
     chat$=""
    Else If key=8
     ;удаляем последнюю букву
     If Len(chat$)>0 Then chat$=Left$(chat$,Len(chat$)-1)
    Else If key>=32
     ;пишем букву
     chat$=chat$+Chr$(key)
    EndIf
   EndIf
  EndIf
 Next
End Function

Function info(t$)
 i.Info=New Info
 i\txt$=t$
 Insert i Before First Info
End Function

Function RenderAll()
 Cls

 Text 10,10,chat$
 y=FontHeight()*2
 r=0;255
 For i.Info=Each Info
  If r<15;максимальное количество ctpok
   Text 8,y,i\txt$
   y=y+FontHeight()
   r=r+1
  Else
   Delete i
  EndIf
 Next
End Function

Function FindPlayer.Player( id )
 For p.Player=Each Player
  If p\net_id=id Then Return p
 Next
End Function
</code><noinclude>[[Категория:Код]]</noinclude>