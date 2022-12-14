Framework brl.glmax2d ' Базовый модуль - движок на основе OpenGL
Import brl.random ' Генератор случайных чисел
Import BRL.Basic ' Из этого модуля используется команда Incbin
Import BRL.PNGLoader ' Загрузка PNG-изображений

Incbin "2DEngine-NewImages.png" ' Сохраняем в exe-файле изображение

Const sxsize = 800, sysize = 600, color_depth = 32 ' Размеры экрана и глубина цвета
Const sxsize2 = sxsize / 2, sysize2 = sysize / 2 ' Вспомогательные константы

Const tilesize = 64 ' Размер тайла / спрайта

Const fxsize = 160, fysize = 120 ' Размеры поля в тайлах 
Global fdx#, fdy# ' Сдвиг отображаемой части поля

Global sc# = 1.0, tilesc# ' Увеличение в пикселах и тайлах
Global dtim# ' Время обработки предыдущего кадра
Const cam_speed# = 2.0 ' Относительная скорость реакции камеры на движения мышью
Const magn_speed# = 2.0 ' Относительная скорость реакции масштаба на вращение колесика мыши
Global camx# = 0.5 * fxsize, camy# =  0.5 * fysize ' Текущие координаты камеры

Const objq = 800

Global layer_order:TList = CreateList() ' Список слоев в порядке отображения

Global frame[fxsize, fysize] ' Номера тайлов для каждой клетки
Const tile_imageq = 3 ' Kол-во тайлов в наборе
Const obj_imageq = 22 ' Kол-во объектов в наборе

' Слой
Type layer_obj Abstract
	Method draw()
	End Method
End Type

' Тайловый слой
Const TILE_DONT_DRAW = -1 ' Kонстанта "Не рисовать тайл"
Type tile_layer_obj Extends layer_obj
	Field image:TImage ' Изображения тайлов
	Field frame[fxsize, fysize] ' Номера тайлов для каждой клетки

	Function add:tile_layer_obj(tile_image:TImage) ' Добавление тайлового слоя
		l:tile_layer_obj = New tile_layer_obj
		l.image = tile_image
		layer_order.addlast l ' Занесение слоя в список отображения
		Return l
	End Function

	Method draw() ' Прорисовка слоя
		SetScale tilesc#, tilesc#
		scr2field 0, 0, x1#, y1#
		scr2field sxsize - 1, sysize - 1, x2#, y2#

		xx1 = Max(0, Floor(x1#)) ' Определение границ куска поля, попадающего в облась зрения
		xx2 = Min(Ceil(x2#), fxsize - 1)
		yy1 = Max(0, Floor(y1#))
		yy2 = Min(Ceil(y2#), fysize - 1)

		For y = yy1 To yy2
			For x = xx1 To xx2
				If frame[x, y] >= TILE_DRAW Then ' Проверка, нужно ли рисовать тайл
					field2scr x, y, sx#, sy#
					DrawImage image, sx#, sy#, frame[x, y]
				End If
			Next
		Next
	End Method
End Type

 ' Слой объектов
Type object_layer_obj Extends layer_obj
	Field objects:TList[fxsize, fysize] ' Список объектов для каждой клетки, находящихся на ней

	Function add:object_layer_obj()
		l:object_layer_obj = New object_layer_obj
		For y = 0 Until fysize ' Инициализация списков
			For x = 0 Until fxsize
				l.objects[x, y] = CreateList()
			Next
		Next
		layer_order.addlast l
		Return l
	End Function

	Method draw()
		scr2field 0, 0, x1#, y1#
		scr2field sxsize - 1, sysize - 1, x2#, y2#

		xx1 = Max(0, Floor(x1# - 0.5))
		xx2 = Min(Floor(x2# + 0.5), fxsize - 1)
		yy1 = Max(0, Floor(y1# - 0.5))
		yy2 = Min(Floor(y2# + 0.5), fysize - 1)

		For y = yy1 To yy2
			For x = xx1 To xx2
				For o:base_obj = EachIn objects[x, y]
					o.draw
				Next
			Next
		Next
		reset_transformations
	End Method
End Type

' Базовый тип для объектов
Type base_obj
	Field x#, y#, size# = 1, angle# ' Kоординаты, размер (в тайлах), угол поворота спрайта объекта
	Field r = 255, g = 255, b = 255 ' Цвет объекта (по умолчанию белый)
	Field image:TImage, frame ' Изображение для объекта, кадр
	Field tile_link:TLink ' Ссылка на этот объект из списка объектов клетки
	Field layer:object_layer_obj ' Слой объекта

	Method random_color() ' Задание случайного (но не очень темного) цвета для объекта
		Repeat
			r = Rand(0, 255)
			g = Rand(0, 255)
			b = Rand(0, 255)
		Until r + g + b >= 255
	End Method

	Method register() ' Занесение объекта в списки (регистрация)
		tilex = Floor(x)
		tiley = Floor(y)
		tile_link = layer.objects[tilex, tiley].addlast(Self) ' Занесение в список объектов клетки
	End Method

	Method draw() ' Рисование объекта
		field2scr x#, y#, sx#, sy#
		SetColor r, g, b
		SetScale size# * tilesc#, size# * tilesc#
		SetRotation angle#
		DrawImage image, sx#, sy#, frame
	End Method
End Type

SeedRnd MilliSecs() ' Для того, чтобы каждый раз получать новую последовательность случайных чисел

SetGraphicsDriver GLMax2DDriver() ' Установка драйвера отображения графики OpenGL
Graphics sxsize, sysize, color_depth
AutoImageFlags FILTEREDIMAGE | MIPMAPPEDIMAGE | DYNAMICIMAGE
SetBlend ALPHABLEND

' Загружаем изображения с альфа-каналом из exe-файла
Global images:TPixmap = LoadPixmapPNG("incbin::2DEngine-NewImages.png")

' Вырезаем изображения
Global tile_images:TImage = tiles_grab(0, 3, False) ' Тайлы
Global obj_images:TImage = tiles_grab(3, 22) ' Объекты

field_generate ' Создаем поле
objects_generate ' Создаем объекты

HideMouse

cx# = camx#
cy# = camy#
'DebugStop
Repeat

	cx# = limit(cx# + (MouseX() - sxsize2) / sc#, 0, fxsize)
	cy# = limit(cy# + (MouseY() - sysize2) / sc#, 0, fysize)
	camera_change cx#, cy#, 1.1 ^ MouseZ() * 64.0

	tim = MilliSecs() ' Засекаем время

	MoveMouse sxsize2, sysize2 ' Установка курсора мыши в центр экрана

	' Прорисовка слоев
	For l:layer_obj = EachIn layer_order
		l.draw
	Next

	For o:layer_obj = EachIn layer_order
		l.draw
	Next

	' Отображение курсора
	reset_transformations
	field2scr cx#, cy#, sx#, sy#
	DrawImage obj_images, sx#, sy#, 21

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

	' Вычисление времени, затраченного на виток цикла (в секундах) для вычисления множителей скоростей
	dtim# = 0.001 * (MilliSecs() - tim)

Until KeyHit(KEY_ESCAPE)

' Генерация поля
Function field_generate()
	layer:tile_layer_obj = tile_layer_obj.add(tile_images)
	For y = 0 Until fysize
		For x = 0 Until fxsize
			layer.frame[x, y] = Rand(0, tile_imageq - 1)
		Next
	Next
End Function

' Генерация объектов
Function objects_generate()
	objs:TList = CreateList()
	layer:object_layer_obj = object_layer_obj.add()
	For n = 1 To objq
		o:base_obj = New base_obj
		o.x = Rnd(0, fxsize - 1)
		o.y = Rnd(0, fysize - 1)
		o.angle# = Rnd(0, 360)
		o.size# = Rnd(0.5, 1.0)
		o.layer = layer
		o.image = obj_images
		o.frame = Rand(0, obj_imageq - 1)
		o.random_color
		o.register
	Next
End Function

' Функция изменения координат камеры и увеличения
Function camera_change(x#, y#, scale#)
	' Приращения координат камеры и увеличения
	sc# = sc# + magn_speed# * (scale# - sc#) * dtim#
	camx# = camx# + cam_speed# * (x# - camx#) * dtim#
	camy# = camy# + cam_speed# * (y# - camy#) * dtim#

	sc# = limit(sc#, Max(1.0 * sxsize / fxsize, 1.0 * sysize / fysize), 256.0) ' Ограничение увеличения
	tilesc# = sc# / tilesize ' Вычисление коэффициента увеличения для тайлов
	
	xsize# = sxsize / sc#	' Размеры отображаемого прямоугольного куска поля
	ysize# = sysize / sc#
	
	fdx# = limit(camx# - xsize# * 0.5, 0, fxsize - xsize#) ' Ограничения смещения поля (по границам)
	fdy# = limit(camy# - ysize# * 0.5, 0, fysize - ysize#)
End Function

' Функция вырезания изображения из другого изображения
Function new_grab:TImage(image:TImage, x, y, frame)
	pixmap:TPixmap = LockImage(image, frame)
	w:TPixmap = images.window(x, y, ImageWidth(image), ImageHeight(image))
	pixmap.paste w, 0, 0
	UnlockImage image
	Return image
End Function

' Функция вырезания тайла или серии тайлов из изображения
Function tiles_grab:TImage(num, frameq = 1, midhn = True)
	image:TImage = CreateImage(tilesize, tilesize, frameq)
	If midhn Then MidHandleImage image ' флаг midhn означает, что изображение нужно отцентровать
	For n = 0 To frameq - 1
		pos = num + n
		new_grab image, (pos Mod 4) * tilesize, Floor(pos / 4) * tilesize, n ' По умолчанию тайлы располагаются на изображении в 4 столбца
	Next
	Return image
End Function

' Функция сброса трансформаций
Function reset_transformations()
	SetColor 255, 255, 255
	SetRotation 0
	SetAlpha 1
	SetScale 1.0, 1.0
End Function

' Перевод из экранных координат в тайловые
Function scr2field(sx#, sy#, tx# Var, ty# Var)
	tx# = sx# / sc# + fdx#
	ty# = sy# / sc# + fdy#
End Function

' Перевод из тайловых координат в экранные
Function field2scr(tx#, ty#, sx# Var, sy# Var)
	sx# = (tx# - fdx#) * sc#
	sy# = (ty# - fdy#) * sc#
End Function

' Ограничение переменной минимальным и максимальным значениями
Function limit#(v#, vmin#, vmax#)
	If v# < vmin# Then v = vmin# ElseIf v# > vmax# Then v# = vmax#
	Return v#
End Function