Strict
Graphics 800,600
Global SparkList:TList = CreateList()
AutoMidHandle(True)
Global iSpark:TImage = LoadImage ("Sparkle.png",MIPMAPPEDIMAGE|FILTEREDIMAGE)
SetBlend LIGHTBLEND

Type TSpark
	Field x#,y#,r,g,b,xv#,yv#,scale#,life
	Function Create(x,y,xv#,yv#,scale#,life)
		Local Spark:TSpark  = New TSpark
		Spark.x = x
		Spark.y = y
		Spark.xv# = xv#
		Spark.yv# = yv#
		spark.r = 255
		spark.g = 255
		spark.b = 255
		spark.scale# = scale#
		spark.life = life
		SparkList.AddLast(Spark)
	End Function
	Method Destroy()
		SparkList.Remove(Self)
	End Method
	Function UpdateAll()
		For Local Spark:TSpark = EachIn SparkList
			spark.yv#:+RndFloat()
			Spark.x#:+Spark.xv#
        	        Spark.y#:+Spark.yv#
        	        spark.r:-1
        	        spark.b:-2
        	        spark.g:-1
        	        SetColor spark.r,spark.g,spark.b
        	        SetScale spark.scale,spark.scale
        	        DrawImage (iSpark,spark.x,spark.y)
        	        SetColor 255,255,255
        	        SetScale 1,1
        	        spark.life:-1
	       	        If spark.life < 1 Then spark.Destroy()
		Next
	End Function
End Type

Repeat
	'Function Create(x,y,xv,yv,scale,life)
	Cls
	'Kогда вы будете удерживать левую кнопку мыши, появится источник частиц
	'Kоторый будет создавать частицы в месте, где находиться курсор
	If MouseDown(1) 	
		'yv - это скорость по оси  OY,равная единице
		'так что y будет меняться  на единицу каждый раз
		Local yv# = 1
		'lx - локальная переменная, являющаяся 
		'вектором скорости по оси OX для частиц
		'Благодаря ей, вы сможете увидеть движение частиц
	        For Local lx# = 0.1 To 1 Step .1
	    	'Для опрятности, сделаем скорость по оси OY - случайной.
	    	yv#:-RndFloat()
	    	'Итак, lx# лежит в диапазоне от 0.1 до 1.Далее создаем две частицы.
	    	' -lx# в последнем случае для того, что бы вторая частица была
                                     'зеркальным отражением первой
	    	TSpark.Create (MouseX(),MouseY()-5,lx#,yv#,.2,660)
	    	TSpark.Create (MouseX(),MouseY()-5,-lx#,yv#,.2,660)
	    Next
	
	End If
	TSpark.UpdateAll
	Flip
Until KeyHit(KEY_ESCAPE)

