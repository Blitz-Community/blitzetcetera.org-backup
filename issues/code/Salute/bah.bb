Type sparcs
Field x#,y#,z#
Field speed#
Field time_life#
Field entity
End Type

Function Create_bah(x#,y#,z#,col)

For i= 1 To col

e.sparcs = New sparcs
e\x=x
e\y=y
e\z=z
e\speed=Rnd(-.4,.4)
e\time_life=Rnd(40,70)
e\entity=LoadSprite("Salute.png",1)
PositionEntity e\entity,x,y,z
TurnEntity e\entity,Rnd(360),Rnd(360),Rnd(360)
Next

End Function

Function Update_bah()

For w.sparcs =Each sparcs

MoveEntity w\entity,0,0,w\speed
w\time_life=w\time_life-1
If w\time_life<=0 Then

FreeEntity w\entity
Delete w
EndIf

Next

End Function