;
; Пример 2: Программа, использующая один рисунок в качестве фонового тайла.
;            Использует отдельный архив с ресурсами.

; [ Terabit Packer ]________________________________________
Include "Bones.bb"        ; Добавим функции упаковщика
PakOutputDir ""            
PakInit      "Data.pak", $9FAF43FB, "TMP", $629FC914 ; Это ваша инициализирующая команда
PakBulkOverWrite = True  ; Установите это в True во время разработки, в False при релизе.

; [ Переменные ]_____________________________________________

Global IMGGrisu  ; переменная хранящая хэндл изображения
Global angle#,r# ; угол и радиус, используемые для вращения текста




; [ Loading Data ]__________________________________________
Function LoadData()

	IMGGrisu=LoadImage(Pak("DATA\GRISU.PNG")) ; Загружает изображение, ничего особенного!
    DlPak()                              ; Удаляет временный файл
 	MaskImage IMGGrisu,255,255,255       ; Создает маску

End Function 


; [ Main Loop ]_____________________________________________

Graphics 640,480,16,2  ; устанавливаем графический режим
SetBuffer BackBuffer() ; устанавливаем буфер
ClsColor 33,57,73      ; Цвет фона
angle#=0:r#=0          ; Обнуляем угол и радиус

LoadData()             ; Загружаем все данные

While Not KeyHit(1)    ; Пока не нажата клавиша ESC, …
	Cls

  If IMGGrisu > 0 Then TileImage IMGGrisu,0,0 ; Рисуем картинку если она есть            

	angle=(angle+1) Mod 360                     ; Рассчитываем вращение
	r=(r+0.5) Mod 300
	Text GraphicsWidth()/2+Cos(angle)*r,GraphicsHeight()/2-Sin(angle)*r,"Grisu's Tutorial No. 1",1,1 ; Добавим немного текста

	Flip
Wend                  ; Конец основного цикла
 
PakClean()            ; Удаляет все ранее использованные ресурсы
End                   ; Выход