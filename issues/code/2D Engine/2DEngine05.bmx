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

Const kolobokq = 500 ' Kол-во диких колобков
Global speedpersec# = 1.0 ' Модификатор скорости (тайлов / сек)
Global angpersec# = 90.0 ' Модификатор угловой скорости (градусов / сек)

Global sc# = 1.0, tilesc# ' Увеличение в пикселах и тайлах
Global dtim# ' Время обработки предыдущего кадра
Global timspeed# ' Модификатор для перемещения с учетом прошедшего времени
Global timang# ' Модификатор для поворота с учетом прошедшего времени
Const minms = 100 ' Ограничитель кадров в секунду для действий объектов
Const cam_speed# = 2.0 ' Относительная скорость реакции камеры на движения мышью
Const magn_speed# = 2.0 ' Относительная скорость реакции масштаба на вращение колесика мыши
Global camx#, camy# ' Текущие координаты камеры

Global layer_order:TList = CreateList() ' Список слоев в порядке отображения
Global actingobj:TList = CreateList() ' Список для активных объектов

Const showcollisions = False ' Показ коллизий (отключен)
Global ccnt, objcnt, chcnt ' Счетчики коллизий, объектов, проверок коллизий в секунду

Const force_reload_time = 7000, force_power# = 3.0 ' Время "перезарядки" Силы, ее мощность
Global force_time = 1000, force_radius# = 5.0 ' Время действия Силы, радиус действия
Global force_reload, force_effect ' Время завершения перезарядки и эффекта Силы

Const fireable_percent = 25 ' Процент стрелющих сухопутных колобков
Const min_fire_distance# = 7.0 ' Минимальная дистанция ведения огня
Const min_enemy_distance = 20 ' Минимальное расстояние до врага в начале игры

Const constant_bonustypeq = 7, temporary_bonustypeq = 5 ' Kол-ва постоянных и временных бонусов
Const constant_bonus_crateq = 10 ' Kол-во ящиков с постоянными бонусами (для каждого)
Const temporary_bonus_crateq = 100 ' Kол-во ящиков с временными бонусами
Const empty_crates_percent = 30 ' Процент пустых ящиков
Const crate_bits_packq = 4 ' Kол-во вариантов кусочков ящика
Const bonustypeq = constant_bonustypeq + temporary_bonustypeq

' Постоянные бонусы
Const BONUS_BULLET_DAMAGE = 0 ' Увеличение повреждений от пуль
Const BONUS_BULLET_SPEED = 1 ' Увеличение скорости пуль
Const BONUS_BULLET_LIFETIME = 2 ' Увеличение времени жизни пули
Const BONUS_RELOAD_TIME = 3 ' Уменьшение интервалов между выстрелами
Const BONUS_MAX_HEALTH = 4 ' Увеличение максимального кол-ва здоровья
Const BONUS_SPEED = 5 ' Увеличение скорости колобка
Const BONUS_ESOURCE = 6 ' Источники энергии (необходимо собрать все для завершения игры)
Global esource_collected, light

' Временные бонусы
Const bonus_threshold = constant_bonustypeq
Const BONUS_HEALTH = bonus_threshold + 0 ' Здоровье
Const BONUS_TEMPORARY_FIREPOWER = bonus_threshold + 1 ' Временное повышение огневой мощи
Const BONUS_BOMB = bonus_threshold + 2 ' Бомба!
Const BONUS_TEMPORARY_SPEED = bonus_threshold + 3 ' Временное ускорение
Const BONUS_TEMPORARY_INVULNERABILITY = bonus_threshold + 4 ' Временная неуязвимость

Global temporary_firepower, temporary_speed, temporary_invulnerability ' Время окончания действия бонусов

Const fading_time = 1000, damage_time = 400 ' Время "затухания", "покраснения" от повреждений
Const NOT_YET = 1000000000, INDESTRUCTIBLE = 1000000000 ' Kонстанты "еще не умер", "неразрушимый"

Const TM_IDLE = 0 ' Играем как обычно
Const TM_READY = 1 ' Готовимся к телепортации (ждем)
Const TM_DECREASING = 2 ' Уменьшаемся
Const TM_ENLARGING = 3 ' Вырастаем на новом месте
Global teleport = NOT_YET, teleport_mode = TM_IDLE ' Время окончания цикла телепортации, режим
Const teleport_ready_time = 5000 ' Время подготовки к телепортации
Const max_teleport_radius = 50 ' Максимальное расстояние в тайлах для телепортации

' Слой
Type layer_obj Abstract
	Field collision_with:TList = CreateList() ' Список слоев, с которыми коллизирует данный

	Method collides_with(layer:layer_obj)
		If tile_layer_obj(layer) Then RuntimeError "Tile layers can't collide - use tile collision layer"
		collision_with.addlast layer
	End Method

	Method draw()
	End Method
End Type

' Тайловый слой
Const TILE_DONT_DRAW = -1 ' Kонстанта "Не рисовать тайл"
Type tile_layer_obj Extends layer_obj
	Field image:TImage ' Изображения тайлов
	Field frame[fxsize, fysize] ' Номера тайлов для каждой клетки

	Method collides_with(layer:layer_obj)
		RuntimeError "Tile layers can't collide - use tile collision layer"
	End Method


	Function add:tile_layer_obj(tile_image:TImage, clearing = True) ' Добавление тайлового слоя
		l:tile_layer_obj = New tile_layer_obj
		l.image = tile_image
		If clearing Then
			For y = 0 Until fysize ' Установка "не рисовать тайл" для всех клеток
				For x = 0 Until fxsize
					l.frame[x, y] = TILE_DONT_DRAW
				Next
			Next
		End If
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

' Слой тайловой коллизии
Type tile_collision_layer_obj Extends layer_obj
	Field collision[fxsize, fysize] ' Kоллизия с тайлом (да / нет)

	Function add:tile_collision_layer_obj()
		Return New tile_collision_layer_obj
	End Function
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

Const CT_IMMATERIAL = 0 ' Тип коллизионной модели - нематериальный
Const CT_CIRCULAR = 1 ' Тип коллизионной модели - круг
Const CT_SQUARE = 2 ' Тип коллизионной модели - квадрат
' Базовый тип для объектов
Type base_obj
	Field x#, y#, size# = 1, angle# ' Kоординаты, размер (в тайлах), угол поворота спрайта объекта
	Field speed# ' Скорость объекта (тайлов / сек)
	Field moving_angle# ' Текущий угол движения
	Field r = 255, g = 255, b = 255 ' Цвет объекта (по умолчанию белый)
	Field image:TImage, frame ' Изображение для объекта, кадр
	Field tilex, tiley ' Kоординаты тайла, на котором находится объект
	Field act_link:TLink, tile_link:TLink ' Ссылки на этот объект из списков активных объектов и объектов клетки
	Field layer:object_layer_obj ' Слой объекта
	Field coll_type = CT_CIRCULAR, radius# = 0.5 ' Тип модели коллизии и ее радиус
	Field health# ' Здоровье объекта
	Field death = NOT_YET, damage_end ' Время смерти (еще не определено), время окончания "покраснения"

	Const ONLY_ON_GROUND = True ' Kонстанта для размещения объекта только на суше
	Method place_find(onlyonground = False) ' Поиск места для размещения объекта
		Repeat
			x = Rnd(1.0, fxsize - 1.01)
			y = Rnd(1.0, fysize - 1.01)
			tilex = Floor(x)
			tiley = Floor(y)
			' Определение сухопутности / подводности колобка
			If layer_sand.collision(tilex, tiley) Then layer = layer_ground_koloboks Else layer = layer_water_koloboks
			' Проверка нахождения на суше (для размещения только на суше) и на отсутствие коллизий
			If layer = layer_ground_koloboks Or onlyonground = False Then If Not collision(x#, y#) Then Exit
		Forever
	End Method

	Method random_color() ' Задание случайного (но не очень темного) цвета для объекта
		Repeat
			r = Rand(0, 255)
			g = Rand(0, 255)
			b = Rand(0, 255)
		Until r + g + b >= 255
	End Method

	Const ACTIVE = True, INACTIVE = False ' Kонстанты "Активный", "Не активный"
	Method register(acting = ACTIVE) ' Занесение объекта в списки (регистрация)
		tilex = Floor(x#)
		tiley = Floor(y#)
		tile_link = layer.objects(tilex, tiley).addlast(Self) ' Занесение в список объектов клетки
		If acting Then act_link = actingobj.addlast(Self) ' Занесение в список активных объектов
		objcnt:+1
	End Method

	Method draw() ' Рисование объекта
		field2scr x#, y#, sx#, sy#
		SetScale size# * tilesc#, size# * tilesc#
		SetRotation angle#

		dmg = damage_end - MilliSecs() ' "Покраснение" от повреждений
		If dmg > 0 Then
			k1# = 1.0 * dmg / damage_time; k2# = 1.0 - k1#
			SetColor k1# * 255 + k2# * r, k2# * g, k2# * b
		Else
			SetColor r, g, b ' Установка естественного цвета
		End If

		If death = NOT_YET Then
			SetAlpha 1 ' Если еще не начал исчезать, то непрозрачный
		Else
			SetAlpha limit(.001 * (death - MilliSecs()), 0, 1) ' Иначе потихоньку исчезает
			If death<MilliSecs() Then destroy ' И в конце уничтожается совсем
		End If

		If Self = player Then
			If temporary_firepower > MilliSecs() Then
				col = 191 + 64 * Sin(MilliSecs()) ' Мерцающий желтый цвет игрока с огневой мощью
				SetColor col, col, 0
			End If
			If temporary_invulnerability > MilliSecs() Then SetAlpha 0.5 ' Полупрозрачность неуязвимого
			Select teleport_mode
				Case TM_READY; SetAlpha 0.75 + 0.25 * Sin(MilliSecs()) ' Циклическое изменение прозрачности во время подготовки к телепортации
				Case TM_DECREASING; s# = sc# * size# / tilesize * Max(0.0, 1.0 * (teleport - MilliSecs()) / fading_time); SetScale s#, s# ' Уменьшение
				Case TM_ENLARGING; s# = sc# * size# / tilesize * Min(1.0, 1.0 - 1.0 * (teleport - MilliSecs()) / fading_time); SetScale s#, s# ' Появление в новом месте
			End Select
		End If

		DrawImage image, sx#, sy#, frame
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

	Method try_move(newx#, newy#)
		If Not collision(newx#, newy#) Then move newx#, newy#; Return True
	End Method

	Method try_move_ang(ang#, spd#, ma_change = False)
		If try_move(x# + timspeed# * Cos(ang#) * spd#, y# + timspeed# * Sin(ang#) * spd#) Then
			If ma_change Then moving_angle# = ang#
			Return True
		End If
	End Method

	Method collision2(o:base_obj, newx#, newy#) ' Проверка объекта на столкновение с другим
		Select True
			Case coll_type = CT_CIRCULAR ' Если модель данного объекта - круг
				Select True
					Case o.coll_type = CT_CIRCULAR ' И модель второго объекта - тоже круг (круг с кругом)
						dx# = newx# - o.x#
						dy# = newy# - o.y#
						' Проверяем, меньше ли расстояние между объектами, чем сумма их радиусов
						If Sqr(dx# * dx# + dy# * dy#) < o.radius# + radius# Then ccnt:+1; Return True
					Case o.coll_type = CT_SQUARE ' А если модель второго объекта - квадрат (круг с квадратом)
						If (o.x# - o.radius# <= newx# And newx# <= o.x# + o.radius#) Or (o.y# - o.radius# <= newy# And newy# <= o.y# + o.radius#) Then
							dx# = Abs(newx# - o.x#)
							dy# = Abs(newy# - o.y#)
							sumr# = o.radius# + radius#
							If dx# < sumr# And dy# < sumr# Then ccnt:+1; Return True
						Else
							dx# = Min(Abs(newx# - o.x# - o.radius#), Abs(newx# - o.x# + o.radius#))
							dy# = Min(Abs(newy# - o.y# - o.radius#), Abs(newy# - o.y# + o.radius#))
							If Sqr(dx# * dx# + dy# * dy#) < radius# Then ccnt:+1; Return True
						End If
					Default ' Но вот если второй объект нематериален - столкновения нет
						Return False
				End Select
			Case coll_type = CT_SQUARE ' Если модель данного объекта - квадрат
				If o.coll_type = CT_SQUARE Then ' И модель второго объекта - тоже квадрат
					dx# = Abs(newx# - o.x#)
					dy# = Abs(newy# - o.y#)
					sumr# = o.radius# + radius#
					' Проверяем, меньше ли модуль разности соотв. координат, чем сумма радиусов
					If dx# < sumr# And dy# < sumr# Then ccnt:+1; Return True
				Else ' Иначе проверяем столкновение второго объекта с данным (меняем местами)
					Return o.collision2(Self, newx#, newy#)
				End If
			Default ' Нематериальный объект не коллизирует
				Return False
		End Select
	End Method

	Method collision(newx#, newy#) ' Проверка данного объекта на столкновение с чем бы то ни было
		' Столкновение с границами поля (это осложнит другие проверки, поэтому выходим)
		If newx# < 1.0 Or newy# < 1.0 Or newx# >= fxsize - 1.0 Or newy# >= fysize - 1.0 Then
			boundaries_collision_act
			Return True
		End If
		For l:layer_obj = EachIn layer.collision_with ' Цикл по всем слоям коллизии
			tl:tile_collision_layer_obj = tile_collision_layer_obj(l)
			If tl Then ' Если слой - тайлово-коллизионный, то
				For yy = Floor(newy# - radius#) To Floor(newy# + radius#)
					For xx = Floor(newx# - radius#) To Floor(newx# + radius#)
						If tl.collision(xx, yy) Then
							tile_object.x# = xx + 0.5
							tile_object.y# = yy + 0.5
							If collision2(tile_object, newx#, newy#) Then collided = True; tile_collision_act xx, yy
						End If
					Next
				Next
			Else ' Иначе слой - объектный, тогда
				ol:object_layer_obj = object_layer_obj(l)
				x2 = Floor(newx#)
				y2 = Floor(newy#)
				For yy = y2 - 1 To y2 + 1
					For xx = x2 - 1 To x2 + 1
						For o:base_obj = EachIn ol.objects[xx, yy]
							If Self<>o Then
								chcnt:+1
								If showcollisions Then ' Показ проверок коллизий линиями
									field2scr o.x#, o.y#, sx1#, sy1#
									field2scr newx#, newy#, sx2#, sy2#
									DrawLine sx1#, sy1#, sx2#, sy2#
								End If
								If collision2(o, newx#, newy#) Then collided = True; object_collision_act o
							End If
						Next
					Next
				Next
			End If
		Next
		Return collided
	End Method

	Method act() ' Действия объектов
	End Method

	Method object_collision_act(o:base_obj) ' Действия при столкновении с объектами
	End Method

	Method tile_collision_act(xx, yy) ' Действия при столкновении с тайлами
	End Method

	Method boundaries_collision_act() ' Действия при столкновении с границами карты
	End Method

	Method damage(amount#) ' Получение повреждений
		If death < NOT_YET Then Return ' Если уже исчезает, то с него хватит
		If health# = INDESTRUCTIBLE Then Return ' Если в принципе неуязвим, тогда тоже выходим
		If Self=player And temporary_invulnerability>MilliSecs() Then Return ' Если временно неуязвим - выходим
		health# = health# - amount# ' Уменьшаем здоровье
		damage_end = damage_time + MilliSecs() ' Задаем "покраснение"
		If health <= 0 Then ' Если здоровье на нуле, то
			death = fading_time + MilliSecs() ' Объект начинает исчезать
			' Ящик исчезает сразу, остальные становятся нематериальными
			If crate_obj(Self)=Null Then coll_type = CT_IMMATERIAL Else death = 0
		End If
	End Method

	Method destroy() ' Kорректное уничтожение объекта
		If act_link<>Null Then RemoveLink act_link ' Удаление объекта из списка активных
		RemoveLink tile_link ' Удаление объекта из списка объектов клетки
		objcnt:-1
	End Method
End Type

Global tile_object:base_obj = New base_obj
tile_object.radius# = 0.5
tile_object.coll_type = CT_SQUARE

' Базовый тип для колобков
Type kolobok_obj Extends base_obj
	Field bullet_reload, bullet_reload_time ' Время окончания перезарядки, время перезарядки
	Field bullet_speed#, bullet_lifetime = 2000 ' Скорость и время жизни пули этого колобка
	Field bullet_damage# ' Повреждения от попадания пулей
	Field max_health# = 1 ' Максимальное здоровье
	Field bite_damage#, bite_reload ' Повреждение от укуса и время возможности следующего укуса
	Field bite_reload_time, bite ' Интервал между укусами, вспомогательный флаг

	Function create:kolobok_obj() ' Создание дикого колобка
		o:kolobok_obj = New kolobok_obj
		o.random_color
		o.image = kolobok
		o.moving_angle# = Rnd(0, 360)
		If Rand(1, 100) > fireable_percent And o.frame = 1 Then ' Параметры для не умеющих стрелять
			o.bullet_reload = 1000000000
			o.bullet_reload_time = 1000
			o.bullet_lifetime = 0
			o.bullet_damage# = 0
			o.bullet_speed# = 0
		Else ' Параметры для умеющих стрелять
			o.bullet_reload_time = Rand(300, 1000)
			o.bullet_lifetime = Rand(1000, 4000)
			o.bullet_damage# = Rnd(1, 5)
			o.bullet_speed# = Rnd(0.5, 1.5)
		End If
		o.max_health = Rand(50, 200)
		o.health = o.max_health
		o.bite_damage# = Rnd(4, 12)
		o.bite_reload_time = Rand(200, 500)
		' Расчет размера и скорости по совокупности параметров
		o.size# = (o.max_health - 50) / 150.0 + o_bullet_speed# / 1.5  + o.bullet_lifetime / 4000.0
		o.size#:+ o.bullet_damage# / 5.0 + (o.bite_damage# - 4.0) / 8.0 + (500 - o.bite_reload_time) / 300.0
		o.size#:+ (1000.0 - o.bullet_reload_time) / 1000.0
		o.size# = limit(o.size / 7.0 * 1.0 + 0.25, 0, 1.0)
		o.speed# = (1.25 - o.size#) * 2
		o.radius# = 0.4 * o.size#
		o.place_find
		o.frame = (o.layer = layer_ground_koloboks) ' Для водных колобков - 0-й кадр, для наземных - 1-й
		o.register
		Return o
	End Function

	Method draw() ' Рисование колобка
		super.draw
		bar_draw
	End Method

	Method bar_draw() ' Рисование полоски здоровья
		field2scr x#, y#, sx#, sy#
		barsize = 1.0 * size# * sc# ' Определение длины (по размеру колобка в пикселах)
		If barsize > 4 And max_health <> health Then
			barsize2 = barsize / 2
			barheight = limit(Floor(max_health / 50) + 1, 1, 6) ' Определение высоты по максимуму здоровья
			SetRotation 0
			SetScale 1, 1
			SetGrayColor 255
			k# = 1.0 * health / max_health
			DrawEmptyRect sx# - barsize2, sy# - barsize2 - 6, barsize-1, barheight + 2
			SetColor 255 * (1.0 - k#), 255 * k#, 0 ' Задание цвета: ближе к максимуму - зеленый, ближе к 0 - красный
			DrawRect sx# - barsize2 + 1, sy# - barsize2 - 5, k# * (barsize - 2), barheight
		End If
	End Method

	Method act() ' Действия колобка
		If death < NOT_YET Then Return ' Если исчезает, то действовать прекращает

		angle# = ATan2(player.y - y#, player.x - x#) ' Угол "наведения" на игрока

		If force_effect > MilliSecs() Then ' Определение расстояния до игрока, если действует Сила
			rad# = Sqr((player.x# - x#) * (player.x# - x#) + (player.y# - y#) * (player.y# - y#))
		Else
			rad# = 10000
		End If
		If rad# <= force_radius# Then ' Если расстояние до игрока меньше радиуса действия Силы, то
			' Пытаемся удалиться от игрока
			try_move_ang angle# + 180.0, force_power# * Sin(90.0 * (force_radius# - rad#) / force_radius#)
		Else
			' Иначе определяем, в какую сторону вращаться
			dang# = calc_dangle(moving_angle#, angle# + 180 * (temporary_firepower > MilliSecs()))
			' И пробуем переместиться, повернувшись
			If Not try_move_ang(moving_angle# + timang# * (1 - 2 * (dang# < 0)), speed#, True) Then
				 ' Если переместиться не удалось, то
				If bite Then ' Если можно укусить, то стоим и кусаем...
					moving_angle# = angle#
					bite = False
				Else ' Если нельзя, то пробуем сместиться в сторону
					If Not try_move_ang(moving_angle# + 90.0 * (1 - 2 * Rand(0, 1)), speed#, True) Then
						 ' Если не получается, пробуем сместиться в другую
						If Not try_move_ang(moving_angle# + 180.0, speed#, True) Then moving_angle# = Rnd(0.0, 360.0)
						' Если совсем нас зажали, в следующий раз попробуем случайный угол
					End If
				End If
			End If
		End If

		If bullet_reload < MilliSecs() Then ' Если пришло время стрелять
			' И расстояние до игрока меньше максимального
			If Sqr((player.x# - x#) * (player.x# - x#) + (player.y# - y#) * (player.y# - y#)) <= min_fire_distance# Then
				' Создаем список и заносим туда всех подыодных колобков
				near:TList = nearly_objects(CreateList(), tilex, tiley, 2, layer_water_koloboks)
				' А также наземных
				near = nearly_objects(near, tilex, tiley, 2, layer_ground_koloboks)
				' Но удаляем себя и игрока
				near.remove player
				near.remove Self
				' Потому что будем проверять, не находится ли другой колобок на пути пули, выпущенной в игрока
				For o:base_obj = EachIn near
					If kolobok_obj(o) Then
						' Вычисляем угол между вектором выстрела и отрезком, соединяющим центры стреляющего и проверяемого колобка
						dang# = Abs(calc_dangle(ATan2(y# - o.y#, x# - o.x#), ATan2(y# - player.y#, x# - player.x#)))
						' Проверяем не меньше ли радиуса колобка длина дуги
						If Pi * Sqr((x# - o.x#) * (x# - o.x#) + (y# - o.y#) * (y# - o.y#)) * dang# / 180.0 < o.radius Then Return
					End If
				Next
				' Если на пути выстрела нет колобков - смело стреляем
				fire
			End If
		End If
	End Method

	Method object_collision_act(o:base_obj)
		If o = player Then ' Проверяем, столкнулись ли с игроком
			If bite_reload < MilliSecs() Then ' И готовы ли кусать
				player.damage(bite_damage) ' Если да, то кусаем
				bite_reload = MilliSecs() + bite_reload_time
			End If
			bite = True ' Флаг показывает, что мы вцепились в игрока и можно стоять на месте
		End If
	End Method

	Method fire()
		' Поправка на скорость при временном ускорении
		If Self=player And temporary_speed > MilliSecs() Then spd# = 6.0 Else spd# = speed#  
		If Self=player And temporary_firepower > MilliSecs() Then ' Стрельба при огневой мощи
			bullet_obj.create x#, y#, 0.75, angle#, 4.0 + spd#, 2000, 25, Self, 0.5 * 0.3, r, g, b
			bullet_reload = MilliSecs() + 40
		Else ' Стрельба в обычном режиме
			bullet_obj.create x#, y#, 0.5 * size#, angle#, bullet_speed# + spd#, bullet_lifetime, bullet_damage, Self, size# * 0.3, r, g, b
			bullet_reload = MilliSecs() + bullet_reload_time
		End If
	End Method

End Type

' Игрок
Type player_obj Extends kolobok_obj
	Function create:player_obj()
		o:player_obj = New player_obj
		o.x# = 0.5 * fxsize ' Помещаем игрока в центре
		o.y# = 0.5 * fysize
		o.size# = 0.75
		o.radius# = 0.4 * o.size
		o.image = kolobok
		o.frame = 2
		o.speed# = 2.0
		o.bullet_reload_time = 450
		o.bullet_speed# = 1.0
		o.bullet_damage# = 2.5
		o.max_health# = 300
		o.health# = o.max_health#
		o.layer = layer_ground_koloboks
		Repeat ' Двигаем игрока вправо, пока он не окажется полностью на суше
			o.x:+0.5
		Until Not o.collision(o.x#, o.y#)
		o.register
		Return o
	End Function

	Method act() ' Действия игрока
		If death < NOT_YET Then Return ' Если уже "замочили", то плюем в потолок
		If teleport_mode = TM_IDLE Then ' Если телепортация не в процессе, то
			If KeyHit(KEY_SPACE) Then ' Если нажат пробел
				If Sqr(targetx# * targetx# + targety# * targety#) <= max_teleport_radius Then ' И расстояние не больше максимума
					If Not collision(player.x# + targetx#, player.y# + targety#) Then ' А также в месте появления нет коллизий
						teleport_mode = TM_READY ' То готовимся к телепортации
						teleport = MilliSecs() + teleport_ready_time ' В течение заданного времени
					End If
				End If
			End If
		Else
			If teleport <= MilliSecs() Then ' Если цикл завершился, то
				teleport_mode = teleport_mode + 1 ' Переходим к следующему
				teleport = MilliSecs() + fading_time ' Задаем время цикла
				If teleport_mode = TM_ENLARGING And Not collision(player.x# + targetx#, player.y# + targety#) Then
					move player.x# + targetx#, player.y# + targety# ' Перемещаемся в точку телепортации после уменьшения
					fdx2# = fdx2# - targetx#
					fdy2# = fdy2# - targety#
					targetx# = 0
					targety# = 0
				ElseIf teleport_mode > TM_ENLARGING Then ' Если цикл увеличения завершен
					teleport_mode = TM_IDLE ' то сбрасываем режим
				End If
			End If
			Return ' При телепортации нужно стоять смирно, поэтому выходим из метода
		End If

		If bullet_reload < MilliSecs() And MouseDown(1) Then fire ' Если подошло время стрелять и нажат "огонь" - стреляем
		
		If MouseDown(2) And force_reload <= MilliSecs() Then ' Используем Силу если нажата кнопка и колобок готов
			force_reload = force_reload_time + MilliSecs()
			force_effect = force_time + MilliSecs()
		End If
		If force_effect > MilliSecs() Then size# = 0.75 + 0.5 * (force_effect - MilliSecs()) / force_time Else size# = 0.75 ' "Пухнем" от Силы

		mov = False ' Определяем угол вектора движения, основываясь на нажатых клавишах
		If KeyDown(KEY_S) Then ang2# = 90.0; mov = True
		If KeyDown(KEY_W) Then ang2# = - 90.0; mov = True
		' Если одна из предыдущих клавиш нажата - модифицируем угол с учетом этого
		If KeyDown(KEY_A) Then ang2# = 180.0 - 0.5 * ang2#; mov = True
		If KeyDown(KEY_D) Then ang2# = 0.5 * ang2#; mov = True
	
		If Not mov Then Return ' Если стоим, то больше здесь делать нечего
	
		' Модификатор скорости для временного ускорения
		If temporary_speed > MilliSecs() Then spd# = 6.0 Else spd# = speed#
		 ' Если нет коллизий, перемещаемся
		try_move_ang ang2#, spd#
	End Method

	Method destroy()
		' Нам задали взбучку - истошно орем
		Notify "AAAAAAAAAAAAAA!!! Whyyyyy???!!!"
		End
	End Method

	Method object_collision_act(o:base_obj)
		' Если столкнулись с бонусом и он еще не взят - берем
		bo:bonus_obj = bonus_obj(o)
		If bo Then If bo.death = NOT_YET Then bo.get
	End Method
End Type

' Пуля
Type bullet_obj Extends base_obj
	Field parent:base_obj, damage# ' Указатель на стреляющего и коэффициент повреждения

	' Создаем пулю: начальные координаты, размер, угол, скорость, время жизни, повреждения, стреляющий, отступ от координат
	Function create:bullet_obj(bx#, by#, bsize#, bangle#, bspeed#, blifetime, bdamage#, bparent:base_obj=Null, d#=0, br=255, bg=255, bb=255)
		bul:bullet_obj = New bullet_obj
		bul.layer = layer_bullets
		bul.x# = bx# + Cos(bangle#) * d# ' Смещение отн. данных координат
		bul.y# = by# + Sin(bangle#) * d#
		bul.r = br
		bul.g = bg
		bul.b = bb
		bul.image = bullet
		bul.parent = bparent
		bul.angle# = bangle#
		bul.size# = bsize# 
		bul.speed# = bspeed#
		bul.radius = bsize# * 0.25
		bul.death = MilliSecs() + blifetime
		bul.damage# = bdamage#
		bul.register
	End Function

	Method act() ' Действует просто - летит вперед до столкновения
		move x# + timspeed# * Cos(angle#) * speed#, y# + timspeed# * Sin(angle#) * speed#
		collision x#, y#
		If MilliSecs() > death Then destroy ' Время жизни ограничено с появления
	End Method

	Method object_collision_act(o:base_obj) ' Повреждение встреченного объекта
		If o <> parent Then
			ccnt:+1
			o.damage(damage)
			destroy
		End If
	End Method
	
	Method boundaries_collision_act() ' Уничтожается при столкновении с границами
		destroy
	End Method
End Type

' Ящик
Type crate_obj Extends base_obj
	Field bonus_type ' Тип бонуса внутри

	Function create:crate_obj(b_type)
		o:crate_obj = New crate_obj
		o.image = crate
		o.place_find ONLY_ON_GROUND
		o.bonus_type = b_type
		o.coll_type = CT_SQUARE
		o.health = 10
		If o.speed >= bonustypeq Then o.speed = -1
		o.register INACTIVE
	End Function

	Method destroy() ' Взрыв ящика
		If bonus_type >= 0 Then bonus_obj.create x#, y#, bonus_type ' Создание бонуса на его месте

		offset = Rand(0, crate_bits_packq - 1) * 16 ' Случайный выбор пакета кусочков
		For yy = 0 To 3 ' Создание 16 разлетающихся кусочков
			For xx = 0 To 3
				o:crate_bits_obj = New crate_bits_obj
				o.dx# = Rnd(-1.0, 1.0) + xx - 1.5
				o.dy# = Rnd(-1.0, 1.0) + yy - 1.5
				o.x# = x# + 0.125 * (xx * 2 - 3)
				o.y# = y# + 0.125 * (yy * 2 - 3)
				o.image = crate_bits
				o.frame = xx + yy * 4 + offset
				o.layer = layer_top_scenery
				o.death = 2000 + MilliSecs()
				o.register
			Next
		Next
		
		super.destroy ' Уничтожение объекта ящика - вызов процедуры из base_obj
	End Method

End Type

' Kусочки ящика
Type crate_bits_obj Extends base_obj
	Field dx#, dy# ' Приращения для движения

	Method act() ' Kусочки просто летят 2 секунды
		x# = x# + dx# * timspeed#
		y# = y# + dy# * timspeed#
	End Method
End Type

' Бонус
Type bonus_obj Extends base_obj
	Field dangle#, rotation_period!, pulsing_period! ' Переменные для шевеления

	Function create:bonus_obj(x#, y#, b_type)
		o:bonus_obj = New bonus_obj
		o.x=x#
		o.y=y#
		o.image = bonus
		o.frame = b_type
		o.health = INDESTRUCTIBLE ' Бонус неуничтожим
		o.dangle# = Rnd(5, 30)
		o.rotation_period! = Rnd(0.5, 0.1)
		o.pulsing_period! = Rnd(0.5, 0.1)
		o.layer = layer_ground_koloboks
		o.register
	End Function

	Method draw()
		angle# = dangle# * Sin(rotation_period! * MilliSecs()) ' Kолебания угла
		size# = 0.8 + 0.2 * Sin(pulsing_period! * MilliSecs()) ' Kолебания размера
		super.draw
	End Method

	Method get() ' Берем бонус
		' Постоянные бонусы изменяют характеристики на значение, зависящее
		' от количества таких бонусов на карте (если собрать все бонусы, то характеристики изменятся
		' от начального до фиксированного значения, они указаны в комментариях
		Select frame
			Case BONUS_BULLET_DAMAGE; player.bullet_damage:+ 10.0 / constant_bonus_crateq ' 2.5 - 12.5 ед
			Case BONUS_BULLET_SPEED; player.bullet_speed:+ 3.0 / constant_bonus_crateq ' 1.0 - 4.0 тайлов / сек
			Case BONUS_BULLET_LIFETIME; player.bullet_lifetime:+ 3000 / constant_bonus_crateq ' 2 - 5 сек
			Case BONUS_RELOAD_TIME; player.bullet_reload_time:- 400 / constant_bonus_crateq ' 0.5 - 0.1 сек
			Case BONUS_MAX_HEALTH; player.max_health:+ 500.0 / constant_bonus_crateq; player.health = player.max_health ' 300 - 800 ед
			Case BONUS_SPEED; player.speed:+ 2.0 / constant_bonus_crateq ' 2.0 - 4.0 тайлов / сек
			Case BONUS_HEALTH
				If player.health = player.max_health Then Return ' Если полное здоровье - бонус не берем
				player.health = limit(player.health + 0.15 * player.max_health, 0, player.max_health) ' +15% от максимума
			Case BONUS_TEMPORARY_FIREPOWER; temporary_firepower=MilliSecs() + 10000 ' 10 секунд огневой мощи
			Case BONUS_TEMPORARY_SPEED; temporary_speed=MilliSecs() + 15000 ' 15 секунд ускорения
			Case BONUS_TEMPORARY_INVULNERABILITY; temporary_invulnerability=MilliSecs() + 20000 ' 20 секунд неуязвимости
			Case BONUS_BOMB
				For n1 = 2 To 4 ' Генерация осколков бомбы
					n2 = 0
					While n2 < 360
						bullet_obj.create x#, y#, 1, n2, n1, (5 - n1) * 800, 35, player, player.size# * 0.4
						n2 = n2 + 10 * (n1 - 1)
					Wend
				Next
			Case BONUS_ESOURCE
				esource_collected = esource_collected + 1
				If esource_collected = constant_bonus_crateq Then light = MilliSecs() + fading_time ' Да будет свет, если собрана вся мана
		End Select
		death = fading_time + MilliSecs() ' Исчезновение бонуса
		coll_type = CT_IMMATERIAL ' Бонус становится нематериальным
	End Method
End Type

SeedRnd MilliSecs() ' Для того, чтобы каждый раз получать новую последовательность случайных чисел

SetGraphicsDriver GLMax2DDriver() ' Установка драйвера отображения графики OpenGL
Graphics sxsize, sysize, color_depth
AutoImageFlags FILTEREDIMAGE | MIPMAPPEDIMAGE | DYNAMICIMAGE
SetBlend ALPHABLEND
reset_transformations

' Загружаем изображения с альфа-каналом из exe-файла
Global images:TPixmap = LoadPixmapPNG("incbin::2DEngine-NewImages.png")

' Создаем текстуры для тайлов
tex_water:TImage = tiles_grab(0, 1, False)
tex_sand:TImage = tiles_grab(1, 1, False)
tex_grass:TImage = tiles_grab(2, 1, False)

' Вырезаем изображения
Global kolobok:TImage = tiles_grab(3, 3)
Global bullet:TImage = tiles_grab(6)
Global bonus:TImage = tiles_grab(7, 12)
Global crate:TImage = tiles_grab(19)
Global crate_bits:TImage = CreateImage(tilesize4, tilesize4, crate_bits_packq * 16)
For n = 0 To 3
	For yy = 0 To 3
		For xx = 0 To 3
			new_grab crate_bits, n * tilesize + xx * tilesize4, yy * tilesize4 + tilesize * 5, n * 16 + yy * 4 + xx
		Next
	Next
Next
Global target:TImage = tiles_grab(24), targetx#, targety#

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
Global layer_bullets:object_layer_obj = object_layer_obj.add() ' Затем пули и осколки бомб
Global layer_water_koloboks:object_layer_obj = object_layer_obj.add() ' После - водные колобки
Global layer_ground_koloboks:object_layer_obj = object_layer_obj.add() ' Потом - наземные колобки, ящики и бонусы
Global layer_top_scenery:object_layer_obj = object_layer_obj.add() ' Сверху - осколки ящиков

' Создаем слои тайловой коллизии
Global layer_water:tile_collision_layer_obj = tile_collision_layer_obj.add() ' Слой "твердой" воды
Global layer_sand:tile_collision_layer_obj = tile_collision_layer_obj.add() ' Слой "твердого" песка

' Определяем что с чем коллизирует
layer_water_koloboks.collides_with layer_water_koloboks ' Водные колобки - между собой
layer_water_koloboks.collides_with layer_sand ' Водные колобки - с коллизионным слоем песка
layer_ground_koloboks.collides_with layer_ground_koloboks ' Сухопутные колобки - между собой
layer_ground_koloboks.collides_with layer_water ' Сухопутные колобки - с коллизионным слоем воды
layer_bullets.collides_with layer_water_koloboks ' Пули - с сухопутными колобками
layer_bullets.collides_with layer_ground_koloboks ' Пули - с водными колобками

field_generate ' Генерируем поле
Global player:player_obj = player_obj.create() ' Создаем игрока
objects_generate ' Создаем колобков и ящики

HideMouse

sc# = 64.0
fdx# = player.x + sxsize2 / sc#
fdy# = player.y + sysize2 / sc#
Repeat

	tim = MilliSecs() ' Засекаем время
	
	MoveMouse sxsize2, sysize2 ' Установка курсора мыши в центр экрана

	' Плавное изменение координат камеры (при телепортации камера фиксируется на игроке, иначе - на средней точке между игроком и мишенью)
	camera_change 0.5 * targetx# * (teleport_mode = TM_IDLE), 0.5 * targety# * (teleport_mode = TM_IDLE), 1.1 ^ MouseZ() * 64.0
	
	player.angle = ATan2(targety#, targetx#) ' Нацеливание спрайта игрока на мишень

	timspeed# = speedpersec# * dtim# ' Определение множителя к скорости на основе прошедшего времени
	timang# = angpersec# * dtim# ' То же для угловой скорости

	' Прорисовка слоев
	For l:layer_obj = EachIn layer_order
		l.draw
	Next

	' Действия активных объектов
	For o:base_obj = EachIn actingobj
		o.act
	Next

	' Отображение счетчиков
	DrawText "Frames/sec:" + fps + ", objects:" + objcnt + ", collision checks/frame:" + chcnt + ", collisions/frame:" + ccnt, 0, 0
	ccnt = 0
	chcnt = 0

	' Отображение мишени
	field2scr targetx# + player.x, targety# + player.y, sx#, sy#
	DrawImage target, sx#, sy#

	' Осветление экрана при сборе всех источников энергии
	If light > MilliSecs() Then
		' Устанавливаем прозрачность
		SetAlpha 1.0 - 1.0 * (light - MilliSecs()) / fading_time
		' И рисуем белый прямоугольник на весь экран
		DrawRect 0, 0, sxsize, sysize
		reset_transformations
	ElseIf light<>0 Then
		' Поздравляем игрока с победой
		Notify "Congratulations!!!"
		End
	End If

	Flip False

	' Обновление счетчика кадров в секунду
	If fpstim<= MilliSecs() Then
		fpstim = MilliSecs() + 1000
		fps = cnt
		cnt = 0
	Else
		cnt:+1
	End If

	If teleport_mode = TM_IDLE Then
		targetx#:+(MouseX() - sxsize2) / sc#	' Изменение координат мишени
		targety#:+(MouseY() - sysize2) / sc#
		' Ограничения на перемещение цели
		targetx# = limit(targetx#, Max(-sxsize / sc# * 0.75, -player.x), Min(sxsize / sc# * 0.75, fxsize - player.x))
		targety# = limit(targety#, Max(-sysize / sc# * 0.75, -player.y), Min(sysize / sc# * 0.75, fysize - player.y))
	End If

	' Вычисление времени, затраченного на виток цикла (в секундах) для вычисления множителей скоростей
	dtim# = 0.001 * (Min(MilliSecs() - tim, minms))
	' Время ограниченно пределом для недопущения слишком больших множителей, отрицательно
	'  сказывающихся на определении столкновений

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
							layer_water.collision[x, y] = True ' Установка коллизии с этим тайлом в водном слое
						ElseIf d# < grass_threshold# Then ' От порога песка до порога травы
							layer_tiles.frame[x, y] = tile_sand ' Пока отображаем чистый тайл песка
							layer_sand.collision[x, y] = True ' Установка коллизии с этим тайлом в слое песка
						Else ' После порога травы
							layer_tiles.frame[x, y] = tile_grass ' Пока отображаем чистый тайл травы
							layer_sand.collision[x, y] = True
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
	layer_water.collision[x, y] = True ' Kоллизия для тайла водного слоя
	layer_sand.collision[x, y] = False ' Нет коллизии для тайла песка
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

' Генерация ящиков и колобков
Function objects_generate()
	' Kолобки
	For n = 1 To kolobokq
		If (n Mod 100) = 0 Then loadingbar "Generating objects...", n, kolobokq * 3
		Repeat
			o:kolobok_obj = kolobok_obj.create()
			' Расстояние до игрока должно быть не меньше минимума
			If Sqr((o.x - player.x) * (o.x - player.x) + (o.y - player.y) * (o.y - player.y)) >= min_enemy_distance Then Exit
			o.destroy
		Forever
	Next
	
	' Ящики с постоянными бонусами
	For n1 = 1 To constant_bonustypeq ' Цикл по всем типам бонусов
		If (n Mod 100) = 0 Then loadingbar "Generating objects...", n1 + constant_bonustypeq, constant_bonustypeq * 3
		For n2 = 1 To constant_bonus_crateq ' Создаем заданное кол-во ящиков каждого типа
			crate_obj.create(n1 - 1)
		Next
	Next

	' Ящики с временными бонусами
	For n = 1 To temporary_bonus_crateq
		loadingbar "Generating objects...", n + temporary_bonus_crateq * 2, temporary_bonus_crateq * 3
		If Rand(1,100) > empty_crates_percent Then
			crate_obj.create Rand(0, temporary_bonustypeq - 1) + bonus_threshold
		Else
			crate_obj.create -1 ' Часть ящиков пусты
		End If
	Next
End Function

' Занесение в список объектов находящихся в пределах заданного кол-ва тайлов
Function nearly_objects:TList(lst:TList, x, y, radius, layer:object_layer_obj)
	For yy = Max(y - radius, 0) To Min(y + radius, fysize - 1)
		For xx = Max(x - radius, 0) To Min(x + radius, fxsize - 1)
			For o:base_obj = EachIn(layer.objects[xx, yy])
				lst.addlast o
			Next
		Next
	Next
	Return lst
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
	SetGrayColor 255
	SetRotation 0
	SetAlpha 1
	SetScale 1.0, 1.0
End Function

Function camera_change(x#, y#, scale#)
	' Приращения координат камеры и увеличения
	sc# = sc# + magn_speed# * (scale# - sc#) * dtim#
	camx# = camx# + cam_speed# * (x# - camx#) * dtim#
	camy# = camy# + cam_speed# * (y# - camy#) * dtim#

	sc# = limit(sc#, Max(1.0 * sxsize / fxsize, 1.0 * sysize / fysize), 256.0) ' Ограничение увеличения
	tilesc# = sc# / tilesize ' Вычисление коэффициента увеличения для тайлов
	
	xsize# = sxsize / sc#	' Размеры отображаемого прямоугольного куска поля
	ysize# = sysize / sc#
	
	fdx# = limit(player.x + camx# - xsize# * 0.5, 0, fxsize - xsize#) ' Ограничения смещения поля (по границам)
	fdy# = limit(player.y + camy# - ysize# * 0.5, 0, fysize - ysize#)
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

' Меняем местами значения двух переменных
Function swap(v1 Var, v2 Var)
	z = v2
	v2 = v1
	v1 = z
End Function

' Изменение минимума и максимума на основе переменной
Function setminmax(v#, vmin# Var, vmax# Var)
	If v#<vmin# Then vmin# = v#
	If v#>vmax# Then vmax# = v#
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

' Вычисление минимальной разности углов
Function calc_dangle#(ang1#, ang2#)
	dang# = ang2# - ang1#
	Return dang# - Floor(dang# / 360 + 0.5) * 360
End Function