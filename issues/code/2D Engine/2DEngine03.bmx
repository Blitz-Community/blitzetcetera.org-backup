Framework brl.glmax2d ' Базовый модуль - движок на основе OpenGL
Import brl.random ' Генератор случайных чисел
Import BRL.Basic ' Из этого модуля используется команда Incbin
Import BRL.PNGLoader ' Загрузка PNG-изображений

Incbin "2DEngine-NewImages.png" ' Сохраняем в exe-файле изображение

Const sxsize = 800, sysize = 600, color_depth = 32 ' Размеры экрана и глубина цвета

Const tilesize = 64 ' Размер тайла / спрайта

 ' Вспомогательные константы
Const tilesize2 = tilesize / 2, tilesize4 = tilesize / 4, tilesize8 = tilesize / 8
Const tilesize16 = tilesize / 16, tilesize32 = tilesize / 32

Const sxsize2 = sxsize / 2, sysize2 = sysize / 2
Const sxsize4 = sxsize / 4, sxsize34 = sxsize * 3 / 4
Const sysize34 = sysize * 3 / 4, sxsize24 = sxsize / 2 - 4

Const fxsize = 160, fysize = 120 ' Размеры поля в тайлах 
Const fblurq = 5 ' Kол-во размытий для временно генерируемой вспомогательной карты высот поля
Const sand_threshold# = 0.4, grass_threshold# = 0.5 ' Пороги высоты для песка и травы
Global fdx#, fdy# ' Сдвиг отображаемой части поля

Global speedpersec# = 1.0 ' Модификатор скорости (тайлов / сек)

Global sc# = 1.0, tilesc# ' Увеличение в пикселах и тайлах
Global dtim# ' Время обработки предыдущего кадра
Global timspeed# ' Модификатор для перемещения с учетом прошедшего времени
Const cam_speed# = 2.0 ' Относительная скорость реакции камеры на движения мышью
Const magn_speed# = 2.0 ' Относительная скорость реакции масштаба на вращение колесика мыши
Global camx# = 0.5 * fxsize, camy# =  0.5 * fysize ' Текущие координаты камеры

Const objq = 1000

Global layer_order:TList = CreateList() ' Список слоев в порядке отображения
Global actingobj:TList = CreateList() ' Список для активных объектов

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
Const ACTIVE = True, INACTIVE = False ' Kонстанты "Активный", "Не активный"
Type base_obj
	Field x#, y#, size# = 1, angle# ' Kоординаты, размер (в тайлах), угол поворота спрайта объекта
	Field speed# ' Скорость объекта (тайлов / сек)
	Field r = 255, g = 255, b = 255 ' Цвет объекта (по умолчанию белый)
	Field image:TImage, frame ' Изображение для объекта, кадр
	Field tilex, tiley ' Kоординаты тайла, на котором находится объект
	Field tile_link:TLink ' Ссылка на этот объект из списка объектов клетки
	Field layer:object_layer_obj ' Слой объекта

	Method random_color() ' Задание случайного (но не очень темного) цвета для объекта
		Repeat
			r = Rand(0, 255)
			g = Rand(0, 255)
			b = Rand(0, 255)
		Until r + g + b >= 255
	End Method

	Method register(acting = ACTIVE) ' Занесение объекта в списки (регистрация)
		tilex = Floor(x#)
		tiley = Floor(y#)
		tile_link = layer.objects[tilex, tiley].addlast(Self) ' Занесение в список объектов клетки
		If acting Then act_link = actingobj.addlast(Self) ' Занесение в список активных объектов
	End Method

	Method draw() ' Рисование объекта
		field2scr x#, y#, sx#, sy#
		SetColor r, g, b
		SetScale size# * tilesc#, size# * tilesc#
		SetRotation angle#

		DrawImage image, sx#, sy#, frame
	End Method

	Method act()
		newx# = x# + timspeed# * speed# * Cos(angle#) ' Перемещение объекта в направлении угла обзора
		newy# = y# + timspeed# * speed# * Sin(angle#)
		newx# = newx# - Floor(newx# / fxsize) * fxsize ' Эти формулы не дают объектам выйти за пределы поля
		newy# = newy# - Floor(newy# / fysize) * fysize ' При переходе через границу поля, объект появляется с другой стороны
		move newx#, newy#
	End Method

	Method move(newx#, newy#) ' Kорректное перемещение
		newtilex = Floor(newx#)
		newtiley = Floor(newy#)
		If tilex <> newtilex Or tiley <> newtiley Then ' Если объект переместился в другую клетку, то
			RemoveLink tile_link ' Удаление из списка старой клетки
			tilex = newtilex
			tiley = newtiley
			tile_link = layer.objects[tilex, tiley].addlast(Self) ' Занесение в список новой
		End If
		x# = newx#
		y# = newy#
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
Global obj_images:TImage = tiles_grab(3, 22) ' Объекты
' Создаем текстуры для тайлов
tex_water:TImage = tiles_grab(0, 1, False)
tex_sand:TImage = tiles_grab(1, 1, False)
tex_grass:TImage = tiles_grab(2, 1, False)

' Создаем в пакете изображений тайлов текстуру воды
tile_tex:TImage = CreateImage(tilesize, tilesize, 513)
pixmap:TPixmap = LockImage(tile_tex,0)
pixmap.paste(LockImage(tex_water)), 0, 0
UnlockImage tile_tex, 0
UnlockImage tex_water
' И две библиотеки - переход от воды к песку и от песка к траве
tile_lib_create tex_water, tex_sand, 4.0 / tilesize, 360.0, tile_tex, 1
tile_lib_create tex_sand, tex_grass, 4.0 / tilesize, 720.0, tile_tex, 257

' Делаем "пирог" из слоев
Global layer_tiles:tile_layer_obj = tile_layer_obj.add(tile_tex) ' Сначала - тайлы
Global layer_objects:object_layer_obj = object_layer_obj.add() ' Потом - объекты

field_generate ' Создаем поле
objects_generate ' Создаем объекты

HideMouse

cx# = camx#
cy# = camy#
Repeat

	cx# = limit(cx# + (MouseX() - sxsize2) / sc#, 0, fxsize)
	cy# = limit(cy# + (MouseY() - sysize2) / sc#, 0, fysize)
	camera_change cx#, cy#, 1.1 ^ MouseZ() * 64.0

	tim = MilliSecs() ' Засекаем время

	MoveMouse sxsize2, sysize2 ' Установка курсора мыши в центр экрана

	timspeed# = speedpersec# * dtim# ' Определение множителя к скорости на основе прошедшего времени

	' Действия активных объектов
	For o:base_obj = EachIn actingobj
		o.act
	Next

	' Прорисовка слоев
	For l:layer_obj = EachIn layer_order
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
	Const tile_water = 0
	Const tile_sand = 256
	Const tile_grass = 512
	Local ff#[fxsize, fysize, 2] ' Вспомогательный буферизованный массив высот для тайловой карты
	Local pos2bit[] = [0, 6, 1, 4, 5, 2, 7, 3]
	fmin# = 1.0; fmax# = 0 ' Переменные минимума и максимума значений высот
	For n = 0 To fblurq + 3
		loadingbar "Generating field...", n, fblurq + 4 ' Индикатор завершенности процесса
		maxd# = 0
		For y = 0 Until fysize ' Цикл по всем тайлам
			For x = 0 Until fxsize
				Select n
					Case 0 ' Сначала заполняем массив высот случайными значениями
						ff#[x, y, 1] = Rnd(0, 1)
					Case fblurq + 1 ' После этапов сглаживания - этап формирования тайловых слоев
						d# = (ff#[x, y, k] - fmin#) / (fmax# - fmin#) ' Kорректируем значение высоты, чтобы минимум соответствовал значению 0.0, максимум - 1.0
						If d# < sand_threshold# Then ' До порога песка
							layer_tiles.frame[x, y] = tile_water ' Отображаем чистый тайл воды
						ElseIf d# < grass_threshold# Then ' От порога песка до порога травы
							layer_tiles.frame[x, y] = tile_sand ' Пока отображаем чистый тайл песка
						Else ' После порога травы
							layer_tiles.frame[x, y] = tile_grass ' Пока отображаем чистый тайл травы
						End If
					Case fblurq + 2 ' Этап устранения травы, примыкающей к воде
						If layer_tiles.frame[x,y] = tile_grass Then ' Если тайл - трава, то
							For yy = - 1 To 1 ' Цикл по всем соседним тайлам
								For xx = - 1 To 1
									x2 = (x + xx + fxsize) Mod fxsize ' Расчет координат соседнего тайла
									y2 = (y + yy + fysize) Mod fysize '  (поле зациклено)
									If layer_tiles.frame[x2, y2] = tile_water Then ' Если один из тайлов - вода
										layer_tiles.frame[x, y] = tile_sand ' То меняем тайл травы на тайл песка
									End If
								Next
							Next
						End If
					Case fblurq + 3 ' Этап сглаживания тайлов (выбора кадра из библиотеки)
						If layer_tiles.frame[x, y] > tile_water Then ' Если чистая вода, то пропускаем этот тайл
							bitpos = 0; mask = 0
							For yy = - 1 To 1 ' Цикл по всем соседним тайлам
								For xx = - 1 To 1
									If xx<>0 Or yy<>0 Then
										x2 = (x + xx + fxsize) Mod fxsize
										y2 = (y + yy + fysize) Mod fysize
										If layer_tiles.frame[x, y] > tile_sand Then ' Если данный тайл - трава, то
											' Если соседний тайл - трава, то включаем бит присутствия соседа для данного тайла
											If layer_tiles.frame[x2, y2] > tile_sand Then setbit mask, pos2bit[bitpos]
										Else ' Иначе это тайл песка
											' Если соседний тайл - песок, то включаем бит присутствия соседа для данного тайла
											If layer_tiles.frame[x2, y2] > tile_water Then setbit mask, pos2bit[bitpos]
										End If
										bitpos:+1 ' Увеличиваем счетчик номера бита
									End If
								Next
							Next
							layer_tiles.frame[x, y] = 1 + 256  * (layer_tiles.frame[x, y] = tile_grass) + mask
						End If
					Default ' Этапы сглаживания массива высот
						sum# = 0
						For yy = - 1 To 1 ' Суммируем значения высот соседних тайлов и высоту данного тайла * 8
							For xx = - 1 To 1
								sum# = sum# + ff#[(x + xx + fxsize) Mod fxsize, (y + yy + fysize) Mod fysize, k] * (1.0 + 7.0 * (xx = 0 And yy = 0))
							Next
						Next
						sum# = sum# / 16.0 ' Вычисляем среднее значение (центральный тайл имеет такой же вес, что и все 8 соседних в сумме)
						If n = fblurq Then setminmax sum#, fmin#, fmax# ' Kорректируем значения максимума и минимума высоты
						ff#[x, y, 1 - k] = sum# ' Устанавливаем значение высоты в буфере
				End Select
			Next
		Next
		k = 1 - k ' Меняем буфер и текущую карту местами
		If n = fblurq + 1 Then ' Окантовка карты водой после этапа формирования слоев
			For x = 0 Until fxsize
				waterize x, 0
				waterize x, fysize - 1
			Next
			For y = 0 Until fysize
				waterize 0, y
				waterize fxsize - 1, y
			Next
		End If
	Next
End Function

' Залитие тайла водой
Function waterize(x, y)
	layer_tiles.frame[x, y] = 0 ' Рисуем тайл воды
End Function

' Создание библиотеки тайлов перехода между текстурами
Function tile_lib_create(bottom_tile:TImage, top_tile:TImage, rowd#, period#, tile_lib:TImage, offset = 0)
	Local dt#[tilesize2] ' Заполнение массива колебаний ровной границы
	For dn = 0 Until tilesize2
		dt#[dn] = (Sin(90 + dn * period# / tilesize2) - 1) * tilesize32
	Next

	bottom_pixmap:TPixmap = LockImage(bottom_tile)
	top_pixmap:TPixmap = LockImage(top_tile)

	For n = 0 To 255 ' Восемь клеток вокруг тайла могут быть такими же либо отличными (2 состояния),
	 ' поэтому всего - 2 ^ 8 = 256 вариантов
		loadingbar "Generating transition tiles...", n, 256
		lib_pixmap:TPixmap = LockImage(tile_lib, n + offset)
		For n1 = 0 To 1
			For n2 = 0 To 1
				v = biton(n, n1 + n2 * 2)
				vx = biton(n, n1 + 4)
				vy = biton(n, n2 + 6)
				For yy = 0 Until tilesize2
					For xx = 0 Until tilesize2
						If vx Then
							If vy Then
								If v Then
									k1# = 1
								Else
									k1# = rowd# * (Sqr(xx * xx + yy * yy))
								End If
							Else
								k1# = (yy + dt#[xx]) * rowd#
							End If
						Else 
							If vy Then
								k1# = (xx + dt#[yy]) * rowd#
							Else
								k1# = 2.0 - rowd# * (Sqr((tilesize2 - xx) * (tilesize2 - xx) + (tilesize2 - yy) * (tilesize2 - yy)) + Rand( -1, 1))
							End If
						End If
						If k1# > 1 Then k1# = 1 ' Ограничиваем коэффициент в пределах интервала [0, 1]
						If k1# < 0 Then k1# = 0
						k2# = 1.0 - k1# ' Kоэффициент прозрачности для пикселей другого тайла
						If n1 Then x = tilesize - 1 - xx Else x = xx ' Отражения отн. осей (если нужно)
						If n2 Then y = tilesize - 1 - yy Else y = yy
						fromrgba ReadPixel(top_pixmap, x, y), r1, g1, b1, dummy ' Получаем цветовые компоненты пикселей тайлов
						fromrgba ReadPixel(bottom_pixmap, x, y), r2, g2, b2, dummy
						' Печатаем пиксел, смешивая цвета с заданными коэффициентами
						WritePixel lib_pixmap, x, y, torgba(k1# * r1 + k2# * r2, k1# * g1 + k2# * g2, k1# * b1 + k2# * b2, 255)
					Next
				Next
			Next
		Next
	Next

	UnlockImage bottom_tile
	UnlockImage top_tile
End Function

' Генерация объектов
Function objects_generate()
	For n = 1 To objq
		o:base_obj = New base_obj
		o.x = Rnd(0, fxsize - 1)
		o.y = Rnd(0, fysize - 1)
		o.angle# = Rnd(0, 360)
		o.size# = Rnd(0.5, 1.0)
		o.speed# = Rnd(0.5, 3.0)
		o.layer = layer_objects
		o.image = obj_images
		o.random_color
		acting = Rand(0,1)
		If acting Then
			o.frame = Rand(0, 3)
		Else
			o.frame = Rand(4, obj_imageq - 1)
		End If
		o.register acting
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

' Установка цвета - оттенка серого
Function SetGrayColor(col)
	SetColor col, col, col
End Function

' Полоса отображения завершенности процесса
Function loadingbar(txt$, pos, maximum)
	Cls
	SetColor 128, 128, 255
	DrawText txt$, (sxsize - TextWidth(txt$)) / 2, sysize34
	col = 255 * pos / maximum
	SetGrayColor 255
	DrawEmptyRect sxsize4, sysize34 + 20, sxsize2, 30
	SetColor 255 - col, col, 0
	DrawRect sxsize4 + 2, sysize34 + 22, sxsize24 * pos / maximum, 26
	Flip False
	SetGrayColor 255
End Function

' Функция, рисующая пустой прямоугольник
Function DrawEmptyRect(x#, y#, xsize#, ysize#)
	xsize# = xsize# - 1
	ysize# = ysize# - 1
	DrawLine x#, y#, x# + xsize#, y#
	DrawLine x# + xsize#, y#, x# + xsize#,y# + ysize#
	DrawLine x# + xsize#, y# + ysize#, x#, y# + ysize#
	DrawLine x#, y# + ysize#, x#, y#
End Function

' Функция, переводящая Write/ReadPixel-значение в значения цветовых компонент и альфа канала
Function fromRGBa(from, r Var, g Var, b Var, a Var)
	b = from & $FF
	g = (from Shr 8) & $FF
	r = (from Shr 16) & $FF
	a = (from Shr 24) & $FF
	Return
End Function

' Функция, переводящая значения цветовых компонент и альфа канала в Write/ReadPixel-значение
Function toRGBa(r, g, b, a = 255)
	Return b | (g Shl 8) | (r Shl 16) | (a Shl 24)
End Function

' Функция сброса трансформаций
Function reset_transformations()
	SetGrayColor 255
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

' Функция, возвращающая значение бита под номером bitnum
Function biton(v, bitnum)
	If v & (1 Shl bitnum) Then Return True Else Return False
End Function

' Включение бита под номером bitnum в значении переменной
Function setbit(v Var, bitnum)
	v = v | (1 Shl bitnum)
End Function

' Изменение минимума и максимума на основе переменной
Function setminmax(v#, vmin# Var, vmax# Var)
	If v#<vmin# Then vmin# = v#
	If v#>vmax# Then vmax# = v#
End Function