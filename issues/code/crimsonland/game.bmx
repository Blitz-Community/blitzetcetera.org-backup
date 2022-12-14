'ЧАСТЬ 1. Инициализация мира.

'======== КОНСТАНТЫ
Const width=1024 , height=768 'Константы расширения
'======== УСТАНОВКА ИГРЫ 
Graphics width,height,32    'Ставим настройки.


'ЧАСТЬ 2. Загрузка медиа.
Global Arrow = LoadImage ("Media/arrow.png")  'Загружаем курсор

Global GUI = LoadImage ("Media/GUIPOINT.png") 'Загружаем гуи для игры.

Global Fonimage = LoadImage ("Media/1.jpg")   'Загружаем текстуру для игры. 

Global BulletImage = LoadImage ("Media/bulletPistol.png")    'Загружаем картинку пули.
MidHandleImage BulletImage 'Координаты картинки монстра по центру.

Global PlayerImage = LoadAnimImage ("Media/1.png",68,40,0,8) 'Загружаем картинку для главного игрока.
MidHandleImage PlayerImage 'Делаем координаты картинки по центру.

Global MonsterImage = LoadAnimImage ("Media/Monster/1.png",100,50,0,20) 'Загрузка картинки монстра.
MidHandleImage MonsterImage 'Координаты картинки монстра по центру.

Global BloodImage = LoadImage ("Media/Blood.png") 
MidHandleImage BloodImage 

Global Button = LoadImage ("Media/Button/button.png")   'Кнопка для начала новый игры.
Global ButtonDown = LoadImage ("Media/Button/buttondown.png") 
MidHandleImage Button 
MidHandleImage ButtonDown


Global ButtonExit =  LoadImage ("Media/Button/buttonexit.png")   'Кнопка для начала новый игры.
Global ButtonExitDown = LoadImage ("Media/Button/buttonexitdown.png") 
MidHandleImage ButtonExit 
MidHandleImage ButtonExitDown


Global SMonster:TList=New TList
Global SPlayer:TList=New TList 'Листы для объектов.
Global SBullet:TList=New TList
Global SBlood:TList=New TList

'ЧАСТЬ 3. Создания типов, переменных, функций, необходимых для работы игры.
Global TipGame 'Для того, чтобы можно было понять, что сейчас происходит. 0- меню. 1- игра.
Global Level = 1000 'Уровень
Global time = 1000
Global a
'======== ДЛЯ УПРАВЛЕНИЯ СПИСКАМИ  
Function UpdateEntities( list:TList )
	For Local entity:TEntity=EachIn list
		entity.Update
	Next
End Function

'======== ТИП ДЛЯ РАБОТЫ С ДРУГИМИ ТИПАМИ
'Чтобы можно было работать с другими типами, необходим этот...
Type TEntity
	Field link:TLink
	Method Remove()
		link.Remove
		Sila:-1
	End Method
	Method AddLast( list:TList )
		link=list.AddLast( Self )
	End Method
	Method Update() Abstract
End Type

Function Dn_DrawImage (image, x#, y#, rotat#,level# ) 'Рисуем картинку через функцию.
	SetBlend Alphablend
	SetRotation rotat
	SetAlpha level
	DrawImage image, x, y
End Function

Function Dn_DrawAnimImage (image, x#, y#, rotat#,level#, f ) 'Рисуем картинку через функцию.
	SetBlend Alphablend
	SetRotation rotat
	SetAlpha level
	DrawImage image, x, y, f
End Function

'======== ОПРЕДЕЛИТЬ УГОЛ МЕЖДУ ТОЧКАМИ    
Function Angle!(x0!,y0!,x1!,y1!)
	Return ATan2(y1-y0,x1-x0)
End Function


'ЧАСТЬ 4. Создания типов для игры

Type TPlayer Extends TEntity 'Объявляем тип.
Field X#,Y#                                'Положение игрока.
Field Life                               'Жизнь.
Field Ang                                'Поворот.
Field frame , animnext                   'Для анимации.
Field Weapon                             'Какое оружие. 0 - автомат. 1 - дробовик
Field Shot                               'Когда был произведен предыдузий выстрел.	
	
	Method Update() 'Цикл, где будут проходить все действия для игрока.
	
	'Движение игрока.
	If KeyDown(Key_W) Then Y = Y - 3 ; If animnext < MilliSecs () Then animnext=MilliSecs()+50 ; frame:+1 
	If KeyDown(Key_S) Then Y = Y + 3 ; If animnext < MilliSecs () Then animnext=MilliSecs()+50 ; frame:+1 
	If KeyDown(Key_A) Then X = X - 3 ; If animnext < MilliSecs () Then animnext=MilliSecs()+50 ; frame:+1 
	If KeyDown(Key_D) Then X = X + 3 ; If animnext < MilliSecs () Then animnext=MilliSecs()+50 ; frame:+1 
	
	Ang = Angle(x,y,MouseX(),MouseY())'Определяем поворот игрока.
	
	If frame=8 Then frame=0
	Dn_DrawAnimImage (playerImage, x, y, ang,1,frame )
	
	If shot < MilliSecs () And MouseDown (1) Then 
	
	If weapon = 0 Then shot = MilliSecs () +  50  ; TBullet.CreateBullet (x,y,ang+Rand(-6,6),30)
	If weapon = 1 Then shot = MilliSecs () +  500 ; For i = 0 To 10 ; TBullet.CreateBullet (x,y,ang+Rand(-40,40),15) ; Next
	End If
	
	If KeyHit (key_f1) Then weapon=0
	If KeyHit (key_f2) Then weapon=1
	
	SetRotation 0
	DrawText life ,0 , 0
	End Method 
	
	
	
	Function CreatePlayer:TPlayer( ) 'Функция для создания игрока.
	Local Player:TPlayer=New TPlayer 'Создаем игрока.
	Player.x = width*0.5 'Делаем, чтобы положение игрока было точно в центре.
	Player.y = width*0.5
	Player.life = 100    '100 жизней для начала. 
	Player.AddLast SPlayer 'Добавляем объект в список
	Return Player 'Возвращаем объект.
	End Function
	
End Type

Type TMonster Extends TEntity 'Объявляем тип.
Field X#,Y#                                'Положение объекта.
Field Life                               'Жизнь.
Field Ang                                'Поворот.
Field frame , animnext					      'Для анимации.

	Method Update() 'Цикл, где будут проходить все действия для монстра.
	If life < 0 Then remove ; Return; 	If Level>300 Then Level:-5
	
	For Player:TPlayer = EachIn SPlayer
		If x > player.x - 30 And x < player.x + 30 And y > player.y - 30 And y < player.y + 30 Then
			
			'CollideImage monsterimage,x,y,0,0,1
			'If CollideImage(playerimage,Player.x,Player.y,0,1,0)
			player.life:-1
			'End If
			TBlood.CreateBlood (player.x,player.y,0.01)
			ResetCollisions
		End If		
		
		
	
		
		Ang = Angle(x,y,player.x,player.y)'Определяем поворот монстра
	Next

	x=x+(3.5*Cos(ang))
	y=y+(3.5*Sin(ang))

	
	If animnext < MilliSecs () Then animnext=MilliSecs()+50 ; frame:+1 
	If frame=20 Then frame=0
	Dn_DrawAnimImage (monsterimage, x, y, ang,1,frame )
	
	End Method 
	
	Function CreateMonster:Tmonster(x#,y#) 'Функция для создания монстра.
	Local monster:TMonster=New TMonster 'Создаем игрока.
	Monster.x = x
	Monster.y = y
	Monster.life = 100    '100 жизней для начала. 
	Monster.AddLast SMonster 'Добавляем объект в список
	Return Monster 'Возвращаем объект.
	End Function
	
End Type



Type TBullet Extends TEntity 'Объявляем тип.
Field X#,Y#                              'Положение пули.
Field Ang                                'Поворот.
Field speed 
                             
	Method Update() 'Цикл, где будут проходить все действия для пули.
	'Если пуля вылетает за пределы карты - удаляем.
	If x<-100 Or x>=width+100 Or y>=height Or y<=-100  Then
	remove
	Return
	EndIf
	
	For Monster:TMonster = EachIn SMonster
		If x > monster.x - 25 And x < monster.x + 25 And y > monster.y - 25 And y < monster.y + 25 Then	
			'CollideImage bulletimage,x,y,0,0,1
			
			'If CollideImage(monsterimage,monster.x,monster.y,0,1,0)
			monster.life:-20
			TBlood.CreateBlood (x,y,0.001)
			'ResetCollisions
			remove
			Return
			'End If
			
		End If		
	
	Next
	
	
	x=x+(speed*Cos(ang))
	y=y+(speed*Sin(ang))
	


	Dn_DrawImage bulletImage, x, y, ang,1 
	End Method 
	
	Function CreateBullet:TBullet(x#,y#,ang#, speed) 'Функция для создания пули.
	Local Bullet:TBullet=New TBullet 'Создаем игрока.
	Bullet.x# = x#
	Bullet.y# = y#
	Bullet.ang = ang
	Bullet.speed = speed
	Bullet.AddLast SBullet 'Добавляем объект в список
	Return Bullet 'Возвращаем объект.
	End Function
	
End Type



Type TBlood Extends TEntity 'Объявляем тип.
Field X#,Y#                                'Положение пули.
Field Ang                                'Поворот.
Field Alpha#                             'Прозрачность крови.
Field AlphaM#                            'Как быстро будет кровь становится прозрачнее

	Method Update() 'Цикл, где будут проходить все действия для крови.
	If alpha < 0 Then  REmove ; Return
	alpha = alpha - alpham
	Dn_DrawImage (BloodImage, x, y, ang,alpha )
	End Method 
	
	Function CreateBlood:TBlood(x#,y#,m#) 'Функция для создания крови.
	Local Blood:TBlood=New TBlood 'Создаем игрока.
	Blood.x = x
	Blood.y = y
	Blood.alpham = m
	Blood.alpha = 1
	Blood.ang = Rand (360)
	Blood.AddLast SBlood 'Добавляем объект в список
	Return Blood 'Возвращаем объект.
	End Function
	
End Type



'ЧАСТЬ 5. Создание меню.

While  TipGame <> 1 'Цикл для меню. Пока переменная не равна 1, будет меню.
Cls

Dn_DrawImage (button, width*0.5, height*0.5, 0,1 )
Dn_DrawImage (buttonexit, width*0.5, height*0.5+50, 0,1 )

If MouseX()> (width*0.5)-((ImageWidth(Button))*0.5) And MouseX()< (width*0.5)+((ImageWidth(Button))*0.5) And MouseY()> (height*0.5)-((ImageHeight(Button))*0.5) And MouseY()< (height*0.5)+((ImageHeight(Button))*0.5) Then
'Вот такой мегааццкий код для коллизии кнопка - курсор.  
'(width*0.5)-((ImageWidth(Button))*0.5) - ширину * 0.5  - ширину кнопки * 0.5  -- определяем начальную х кнопки.
'(width*0.5)+((ImageWidth(Button))*0.5) - ширину * 0.5  + ширину кнопки * 0.5  -- определяем конечную х кнопки.
'и так же для высоты кнопки ;)
Dn_DrawImage (buttondown, width*0.5, height*0.5, 0,1 )
End If

If MouseX()> (width*0.5)-((ImageWidth(Button))*0.5) And MouseX()< (width*0.5)+((ImageWidth(Button))*0.5) And MouseY()> (height*0.5)-((ImageHeight(Button))*0.5) And MouseY()< (height*0.5)+((ImageHeight(Button))*0.5)And MouseDown (1) Then TipGame = 1

If MouseX()> (width*0.5)-((ImageWidth(Button))*0.5) And MouseX()< (width*0.5)+((ImageWidth(Button))*0.5) And MouseY()> (height*0.5)-((ImageHeight(Button))*0.5)+50 And MouseY()< (height*0.5)+((ImageHeight(Button))*0.5)+50 Then
Dn_DrawImage (buttonexitdown, width*0.5, height*0.5+50, 0,1 )
End If

If MouseX()> (width*0.5)-((ImageWidth(Button))*0.5) And MouseX()< (width*0.5)+((ImageWidth(Button))*0.5) And MouseY()> (height*0.5)-((ImageHeight(Button))*0.5)+50 And MouseY()< (height*0.5)+((ImageHeight(Button))*0.5)+50 And MouseDown (1) Then End

Flip
Wend







'ЧАСТЬ 6. Цикл для игры.
TPlayer.CreatePlayer ()

While Not KeyDown (KEy_Escape)
Cls
	
	TileImage Fonimage , 0 , 0

	UpdateEntities Sblood
	UpdateEntities Sbullet 
	UpdateEntities Splayer 
	UpdateEntities Smonster

	
	If time < MilliSecs () Then 
	time = MilliSecs () + level 
	a = Rand (1,4)
	Select a
	Case 1
	TMonster.CreateMonster(-50, Rand(height))
	Case 2
	TMonster.CreateMonster (width+50, Rand(height))
	Case 3
	TMonster.CreateMonster (Rand(width) , -50)
	Case 4
	TMonster.CreateMonster (Rand(width), height+50)
	End Select
	End If
Flip
Wend




