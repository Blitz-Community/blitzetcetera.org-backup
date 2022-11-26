'Объекты-капли в BlitzMax, используя только Max2D 

'Диаметр шара и его половина
Local ballsize:Int=512
Local ballsizehalf:Int=ballsize/2 

'Устанавливаем графический режим
Graphics 800,600,0

Cls 

'Вычисляем делители для разных диаметров шара
Local balldivider:Float
If ballsize=128 Then balldivider=64 '8x8
If ballsize=256 Then balldivider=256 '16x16
If ballsize=512 Then balldivider=1024 '32x32
Local lineardivider:Float
If ballsize=128 Then lineardivider=0.5
If ballsize=256 Then lineardivider=1
If ballsize=512 Then lineardivider=2

'Генерация картинки с полем
For Local r:Float=1 To ballsize-1 Step 0.5
	Local level:Float=r
	level:*level
	level=level/balldivider
	SetColor level,level,level 'Нужный градиент
	'SetColor r/lineardivider,r/lineardivider,r/lineardivider 'Для линейных градиентов
	
DrawOval r/2,r/2,ballsize-r,ballsize-r  
Next

'Собираем в картинку
AutoMidHandle True
Local img:TImage=CreateImage(ballsize,ballsize,1,FILTEREDIMAGE)
GrabImage(img,0,0,0)


'Устанавливаем режим рисования
SetBlend LIGHTBLEND

'Рисуем картинку, пока не нажмут на escape
Repeat
	Cls
	DrawImage img,400,300
	DrawImage img,MouseX(),MouseY()
	Flip
Until KeyHit(KEY_ESCAPE)