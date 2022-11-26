;устанавливаем экран и высчитываем его центр
Const screenWidth = 800, screenHeight = 600
Graphics screenWidth, screenHeight, 16, 2
centerX = screenWidth / 2
centerY = screenHeight / 2

;Задаем размер ковра и порог для рекурсивной фунуции
Const carpetSize = 600			;задаем размер ковра
Const threshold = 4				;задаем порог рекурсии

;Рисуем белый квадрат посередине экрана, размером с ковер
offset = carpetSize / 2
Rect centerX - offset, centerY - offset, carpetSize, carpetSize

;ставим цвет рисования черным и приступаем к рекурсии...
Color 0, 0, 0
carpet(centerX, centerY, carpetSize, threshold)

;СЁ, готово! После нажатия на любую кнопку прога завершается 
WaitKey
End


 
;рекурсивная функция
Function carpet(x, y, size, threshold)
	newSize = size / 3		;1/3 предыдущего размера
	offset = newSize / 2		;новое значение отступа

	;рисуем черный квадрат в центре(это и есть "родитель", центральный)
	Rect x - offset, y - offset, newSize, newSize

	If threshold > 0 Then
		newThreshold = threshold - 1	;уменяшаем порог - не забудь про это!
		
		;зовем функцию ковра 8 раз для каждого квадратика вокруг центрального 
		carpet(x - newSize, y - newSize, newSize, newThreshold)
		carpet(x, y - newSize, newSize, newThreshold)
		carpet(x + newSize, y - newSize, newSize, newThreshold)
		carpet(x + newSize, y, newSize, newThreshold)
		carpet(x + newSize, y + newSize, newSize, newThreshold)
		carpet(x, y + newSize, newSize, newThreshold)
		carpet(x - newSize, y + newSize, newSize, newThreshold)
		carpet(x - newSize, y, newSize, newThreshold)
	End If
End Function