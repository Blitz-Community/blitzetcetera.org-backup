Const sxsize = 800, sysize = 600, color_depth = 32 ' Размеры экрана и глубина цвета

Const tilesize = 64 ' Размер тайла / спрайта

Const fxsize = 80, fysize = 60 ' Размеры поля в тайлах 

Const objq = 200

Global frame[fxsize, fysize] ' Номера тайлов для каждой клетки
Const imageq = 25 ' Kол-во изображений в наборе
Const tileq = 3 ' Kол-во тайлов в наборе

Type obj
 Field x, y, frame, angle#, size#, r, g, b
End Type

SeedRnd MilliSecs() ' Для того, чтобы каждый раз получать новую последовательность случайных чисел

SetGraphicsDriver GLMax2DDriver() ' Установка драйвера отображения графики OpenGL
Graphics sxsize, sysize, color_depth
SetMaskColor 0, 0, 0

Cls
DrawImage LoadImage("2DEngine-Images.png"), 0, 0
images = CreateImage(tilesize, tilesize, imageq)

' Вырезаем текстуры для тайлов
For n = 0 To imageq - 1
	GrabImage images, tilesize * (n Mod 4), tilesize * Floor(n / 4), n
Next

' Генерируем поле
For y = 0 Until fysize
	For x = 0 Until fxsize
		frame[x, y] = Rand(0, tileq - 1)
	Next
Next

objs:TList = CreateList()
For n = 1 To objq
	o:obj = New obj
	o.x = Rand(0, (fxsize - 1) * tilesize)
	o.y = Rand(0, (fysize - 1) * tilesize)
	o.size# = Rnd(0.5, 1.0)
	o.angle# = Rnd(0.0, 360.0)
	o.r = Rand(0, 255)
	o.g = Rand(0, 255)
	o.b = Rand(0, 255)
	o.frame = Rand(tileq, imageq - 1)
	objs.addlast o
Next

HideMouse

Repeat

	For y = 0 Until fysize
		yy = tilesize * y + fdy
		For x = 0 Until fxsize
			xx = tilesize * x + fdx
			DrawImage images, xx, yy, frame[x, y]
		Next
	Next

	For o:obj = EachIn objs
		SetColor o.r, o.g, o.b
		SetRotation o.angle#
		SetScale o.size#, o.size#
		DrawImage images, o.x + fdx, o.y + fdy, o.frame
	Next
	SetColor 255, 255, 255
	SetScale 1.0, 1.0
	SetRotation 0.0
	
	fdx = fdx + 4 * (KeyDown(KEY_LEFT) - KeyDown(KEY_RIGHT))
	fdy = fdy + 4 * (KeyDown(KEY_UP) - KeyDown(KEY_DOWN))

	' Обновление счетчика кадров в секунду
	If fpstim<= MilliSecs() Then
		fpstim = MilliSecs() + 1000
		fps = cnt
		cnt = 0
	Else
		cnt:+1
	End If
	DrawText "Frames/sec:" + fps, 0, 0
	
	Flip False

Until KeyHit(KEY_ESCAPE)