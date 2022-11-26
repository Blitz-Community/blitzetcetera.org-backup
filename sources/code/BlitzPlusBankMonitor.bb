glueball=CreateBank(1571)

For t=0 To 1551
	PokeByte glueball,t,t Mod 256
Next

MonitorBank glueball

FreeBank glueball
End


Function MonitorBank(bank,action$="",dat$="")


	;
	; MonitorBank, Blitzplus
	;
	; by CS_TBL
	;
	; usage:	MonitorBank MyBank
	;
	; The function is completely stand-alone
	; LeftMouseButton on the canvas: cycle through decimal-, hexademical- and ascii-viewmodes
	; Out of lazyness, not all ASCII codes below 32 are supported, also because of a lack of pixels.. :-)
	;


	; look all !
	
	If Not bank Return

	Select action$
	
		Case "" ; setup/(main)/end

			FlushEvents()
		
			; 00	04	inputbank
			; 04	04	fontimage
			; 08	04	window
			; 12	04	canvas
			; 16	04	scrollbar
			; 20	04	offset
			; 24	04	mainpanel
			; 28	01	update, not really used ... well, .. used .. but without any benefits..
			; 29	01	show_highlighter
			; 30	01	hex/dec/ascii
			; 31	01	x
			; 32	01	y
			; 33	02	printx
			; 35	02	printy
			
			bnk=CreateBank(37)
			PokeInt bnk,0,bank

			s$=""
			s$=s$+"128176#~#J'~#?$&amp;$'$'%)%)%)%)%)%)%&amp;%&amp;%$$$$$&amp;$$&amp;&amp;$$$$$&amp;$&amp;$&amp;)$&amp;%%$&amp;%)$'$&amp;'''''''''''''%$$$$$&amp;$$$%$%$&amp;$&amp;$$$%$%$&amp;$+$%$&amp;$$$$$'$&amp;$'$%)%"
			s$=s$+")%)%)%)%)%)$%&amp;$%&amp;%$%$&amp;%%$$$%$%%%%($$$&amp;$%$$$%$&amp;$&amp;$'$%)%)%)%)%)%)%)$$$$&amp;$$$$$%$%$&amp;$'$&amp;$%$&amp;$($%$'$$$$$&amp;$%$&amp;$'$%)%)%)%)%)%)%)$%%%%$$"
			s$=s$+"$%$%&amp;$$'$&amp;$%$&amp;$'(%%&amp;$%%&amp;$''''''''''''''''O$C%)%)%)%)%)%)%Q$~#~#~#~#~#p%%$$%&amp;%)%)%)%)%)%)%)%)%)%&amp;%$%%$&amp;%)%)%)%&amp;$$$$$$$&amp;''''''''''"
			s$=s$+"'''''''''%$%$%$&amp;'''''''%$$$$$$%$)%)%)%)%)%)%)%)%)%)$%$%$$%)%)%)%)$$$$$$$$%)%)%)%)%)%)%)%)%)%)$$&amp;$$$%)%)%)%)$%%'$)%)%)%)%)%)%)%)%"
			s$=s$+")%)$%$%%$$)%)%)%).'''''''''''''''''''/'''''''0%)%)%)%)%)%)%)%)%)%1%)%)%)%~#~#~#8$B%(%b%'%$%.)(%&amp;&amp;*%(%*%R%0%'%$%'$%$%%$$'%%%%%%$)"
			s$=s$+"%'%,%($K%0%'$%$')$%$$'%$%&amp;%%$)$(%,%&amp;$$$$$($B%1%0$%$&amp;((%'&amp;0%,%'&amp;)$B%1%0$%$($$%'$'&amp;1%,%($((.(0$2%/)'$$%&amp;%'%$$%$-%,%'&amp;)$A%A$%$($$%&amp;"
			s$=s$+"%$%$%%%.%,%&amp;$$$$$($A%2%6)&amp;%%%$%%%/%*%)$2%1%'%3%9$(%)'$$/%(%:%1%'%~#/%@((%'(&amp;(%%&amp;%$*%(%*%(&amp;(U%&amp;%&amp;&amp;&amp;%&amp;%$%&amp;%$%&amp;%$%)%&amp;%$%&amp;%$%&amp;%$%&amp;%N"
			s$=s$+"'%%&amp;%%'+%)%$%&amp;%$%)%.%$%&amp;%$%&amp;%8%.%)%%%$%%&amp;'%*%*%$%&amp;%$%)%-%%%&amp;%$%&amp;%7%0%(%%%$%$''%)%)&amp;%*$)%))%&amp;(&amp;)'%)%(%(((%+%$'$%'%(%,%)%)%$%&amp;%'%&amp;"
			s$=s$+"%&amp;%)%'%)%'%4%)%%&amp;%%'%'%-%)%)%$%&amp;%'%&amp;%&amp;%)%6%(((%)%&amp;%&amp;%'%&amp;%.%)%)%$%&amp;%'%&amp;%&amp;%)%7%0%*%&amp;%&amp;%'%&amp;%)%&amp;%)%$%&amp;%$%&amp;%'%&amp;%&amp;%$%&amp;%'%)%*%.%1(''%*%"
			s$=s$+"(*%%(&amp;((%'(&amp;((%)%A%~#%%Q&amp;&amp;)&amp;(%)%*$*%(%%&amp;%'%(($%&amp;%$%)%&amp;%$%&amp;%%((&amp;&amp;%$%%%&amp;%$%&amp;%$%&amp;%$%)%)%&amp;%$%&amp;%'%+%$%%%%%)&amp;$&amp;$&amp;%%$%&amp;%&amp;$&amp;$$%&amp;%$%&amp;%$%)"
			s$=s$+"%&amp;%$%)%)%)%&amp;%'%+%$%$%&amp;%)*$&amp;%%$%&amp;%%$'$$%&amp;%$%&amp;%$%)%&amp;%$%)%)%)%&amp;%'%+%$''%)*$'$%$%&amp;%$$%%$$$*$)%%)%&amp;%$(&amp;(&amp;%)*'%+%$&amp;(%)%$$$%$'$%$%&amp;%$$$"
			s$=s$+"$$$$$$%&amp;%$%&amp;%$%)%&amp;%$%)%)%%&amp;$%&amp;%'%+%$''%)%&amp;%$%$'$%&amp;%$$$$$$$$$%&amp;%$%&amp;%$%)%&amp;%$%)%)%&amp;%$%&amp;%'%+%$%$%&amp;%)%&amp;%$%$'$%&amp;%$$%$$%$%&amp;%$%&amp;%$%)%&amp;%$"
			s$=s$+"%)%)%&amp;%$%&amp;%'%+%$%%%%%)%&amp;%$%%&amp;$%&amp;%%$)%&amp;%$%&amp;%$%&amp;%$%&amp;%$%)%)%&amp;%$%&amp;%'%&amp;%&amp;%$%&amp;%$%)%&amp;%$%%&amp;$%&amp;%&amp;($%&amp;%$)&amp;(%)%*$%*(%%&amp;%'%'(%%&amp;%$*$%&amp;%$%&amp;%%"
			s$=s$+"(~#J)&amp;(%)&amp;(%*$%&amp;%$%&amp;%$%&amp;%$%&amp;%$%&amp;%$*%(.(($/%&amp;%$%&amp;%$%&amp;%$%&amp;%&amp;&amp;&amp;%&amp;%$%&amp;%$%&amp;%$%&amp;%$%&amp;%)%%%)%,%'$$$.%&amp;%$%&amp;%$%&amp;%$%+&amp;&amp;%&amp;%$%&amp;%$%&amp;%%%$%%%&amp;%)"
			s$=s$+"%%%)%,%&amp;$&amp;$-%&amp;%$%&amp;%$%&amp;%$%+&amp;&amp;%&amp;%$%&amp;%$%&amp;%%%$%%%&amp;%(%&amp;%*%+%5)%%&amp;%$)&amp;('&amp;&amp;%&amp;%$%&amp;%$%$$$%&amp;&amp;&amp;%&amp;%'%'%*%+%5%)%&amp;%$%$%+%&amp;&amp;&amp;%&amp;%$%&amp;%$*&amp;&amp;')&amp;%(%+"
			s$=s$+"$+%5%)%&amp;%$%%%*%&amp;&amp;&amp;%&amp;%%%$%%*%%$%*%%%)%+%*%5%)%%%%%%%*%&amp;&amp;&amp;%&amp;%%%$%%*%%$%*%$%*%+%*%5%)%%&amp;$%&amp;%$%&amp;%&amp;&amp;&amp;%&amp;%&amp;&amp;&amp;&amp;$&amp;$%&amp;%$%&amp;%$%*%,%)%5%*&amp;$%$"
			s$=s$+"%&amp;%%('&amp;'(($'%&amp;%$%&amp;%%(%*%()%&amp;(-*~#J%~#G%0&amp;3&amp;/&amp;.&amp;4%%&amp;)&amp;A%0%3%/%$%.%*%0%*%B$0%3%/%1%2'&amp;%%$'%H&amp;(%)''''''%)&amp;$$&amp;''&amp;+%&amp;%$$(%'$$$$$&amp;$$&amp;'"
			s$=s$+"'1%''&amp;%%%%%$%&amp;%%%%''%%%&amp;%$%'%+%&amp;'(%')%&amp;$%%%%%.''%$%%%)%$%&amp;)&amp;%(%%%&amp;%$%'%+%&amp;&amp;)%'%$$$$%%&amp;$%%%%-%$%'%$%%%)%$%&amp;%*%)(&amp;%$%'%+%&amp;'(%'%$$$"
			s$=s$+"$%%&amp;$%%%%-%$%'%$%%%%%%%$%&amp;%%%&amp;%,%&amp;%$%'%+%&amp;%$%'%'%$$$$%%&amp;$%%%%.&amp;$$%$$&amp;'''&amp;$$&amp;''%(%%%%&amp;$%&amp;'&amp;%%%%&amp;%$&amp;'&amp;%$$$$%%&amp;$&amp;'_'7'~#M&amp;(%'&amp;~#6%*"
			s$=s$+"%)%(%Q%Y%*%)%'$$%$$N%Y%*%)%*%.%$%'%$%%%$%'(%''%%%%%%%%%$$$$%%%%%%%%%)%%+%*%6&amp;$%%%$&amp;%&amp;$%%%*%(%%%%%%%%%$$$$&amp;'&amp;%%%(%&amp;%+%*%6&amp;$%%%$&amp;%"
			s$=s$+"%*''%(%%%%%%%%%$$$$'%'%%%'%(%*%)%7&amp;$%%%$&amp;%%-%&amp;%(%%%%%%%%%$$$$'%((&amp;%)%*%)%7%$%'%$%%%)%%%&amp;%$%%%%%&amp;'&amp;)&amp;'*%%%*%*%)%7%-%%%*'(&amp;'&amp;$%&amp;%("
			s$=s$+"$$%&amp;%%%%%%%%)'&amp;(%'&amp;8%-%^'~#~#~#G%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%('''''''''''''''''''''''''''''''&amp;)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%"
			s$=s$+")%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)&amp;'''''''''''''''''''''''''''''''(%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%~"
			s$=s$+"#~#~#~#~#b%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%('''''''''''''''''''''''''''''''&amp;)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%"
			s$=s$+")%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)&amp;'''''''''''''''''''''''''''''''(%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%~#~#~#~#~#b%)%)%)%)%)%)"
			s$=s$+"%)%)%)%)%)%)%)%)%)%('''''''''''''''''''''''''''''''&amp;)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%"
			s$=s$+")%)%)%)%)%)%)%)%)%)&amp;'''''''''''''''''''''''''''''''(%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%~#~#~#~#~#b%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%(''"
			s$=s$+"'''''''''''''''''''''''''''''&amp;)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)&amp;''"
			s$=s$+"'''''''''''''''''''''''''''''(%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%~#~#~#~#~#b%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%(''''''''''''''''''''''''"
			s$=s$+"'''''''&amp;)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)&amp;''''''''''''''''''''''''"
			s$=s$+"'''''''(%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%~#~#~#~#~#b%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%('''''''''''''''''''''''''''''''&amp;)%)%)%)%)%)%)%"
			s$=s$+")%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)&amp;'''''''''''''''''''''''''''''''(%)%)%)%)%)%)%)"
			s$=s$+"%)%)%)%)%)%)%)%)%~#~#~#~#~#b%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%('''''''''''''''''''''''''''''''&amp;)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%"
			s$=s$+")%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)&amp;'''''''''''''''''''''''''''''''(%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%~#~#~"
			s$=s$+"#~#~#b%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%('''''''''''''''''''''''''''''''&amp;)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%"
			s$=s$+")%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)&amp;'''''''''''''''''''''''''''''''(%)%)%)%)%)%)%)%)%)%)%)%)%)%)%)%~#~#p"			

			w=Mid$(s$,1,3):h=Mid$(s$,4,3)
			img=CreateImage(w,h)
			
			SetBuffer ImageBuffer(img)
				c=1
				For t=0 To Len(s$)-1
					char$=Mid$(s$,6+t+1,1):amount=Asc(char$)-35
					If amount>0
						If c
							Color 24,48,96
						Else
							Color 0,0,0
						EndIf 
						For k=0 To amount-1
							Plot x,y:x=x+1
							If x>=w
								x=0:y=y+1
							EndIf 
						Next
						c=1-c
					Else
						c=1-c
					EndIf 
				Next 	
			SetBuffer DesktopBuffer()
			PokeInt bnk,4,img

			cw=ClientWidth(Desktop()):ch=ClientHeight(Desktop())
			window=CreateWindow("BankMonitor - by CS_TBL    [Banksize:"+BankSize(bank)+"]",cw/2-310,ch/2-170,620,340,0,1)
			PokeInt bnk,8,window
			
			mainpanel=CreatePanel(0,0,ClientWidth(window),ClientHeight(window),window)
			SetPanelColor mainpanel,176,192,208
			PokeInt bnk,24,mainpanel

			canvas=CreateCanvas(2,2,578,310,mainpanel)
			PokeInt bnk,12,canvas

			scrollbar=CreateSlider(580,2,16,272,mainpanel,0)
			SetSliderRange scrollbar,16,(BankSize(bank)+15)/16
			PokeInt bnk,16,scrollbar
			If BankSize(bank)<=256 DisableGadget scrollbar

			PokeByte bnk,28,1
			MonitorBank bnk,"update"
			MonitorBank bnk,"main"

			; free-up
			FreeImage img
			FreeBank bnk
			FreeGadget window
			FlushEvents()
			SetBuffer DesktopBuffer()



		Case "main"

			inputbank=PeekInt(bank,0)
			window=PeekInt(bank,8)
			scrollbar=PeekInt(bank,16)
			canvas=PeekInt(bank,12)

			Repeat
			
				WaitEvent()
				
				If EventID()=$803
					If EventSource()=window
						quit=True
					EndIf
				EndIf

				If EventID()=$401
					offset=SliderValue(scrollbar)
					PokeInt bank,20,offset
					PokeByte bank,28,1
					MonitorBank bank,"update"
				EndIf

				If EventSource()=canvas
					If EventID()=$203
						x=(EventX()-66)/32
						y=(EventY()-16)/16
						PokeByte bank,31,x
						PokeByte bank,32,y
						If x>=0 And y>=0
							PokeByte bank,28,1
							PokeByte bank,29,1
							MonitorBank bank,"update"
						Else
							PokeByte bank,29,0
							MonitorBank bank,"update"
						EndIf
					EndIf
					If EventID()=$206
						PokeByte bank,29,0
						MonitorBank bank,"update"
					EndIf
					If EventID()=$201
						If EventData()=1
							hexdec=PeekByte(bank,30)
							hexdec=hexdec+1
							If hexdec>2 hexdec=0
							PokeByte bank,30,hexdec
							MonitorBank bank,"update"
						EndIf
					EndIf
				EndIf

			Until quit




		Case "update"

			inputbank=PeekInt(bank,0)
			canvas=PeekInt(bank,12)
			offset=PeekInt(bank,20);/16
			size=BankSize(inputbank)
			show_highlighter=PeekByte(bank,29)
			hexdec=PeekByte(bank,30)

			mx=PeekByte(bank,31)
			my=PeekByte(bank,32)

			If size
				SetBuffer CanvasBuffer(canvas)
					ClsColor 176,192,208:Cls
	
					For t=0 To 15
					
						st$="+"+Replace$(RSet$(t,2)," ","0")
						PokeShort bank,33,72+t*32
						PokeShort bank,35,4
						MonitorBank bank,"print",st$
	
						st$=Replace$(RSet$((offset+t)*16,8)," ","0")
						PokeShort bank,33,2
						PokeShort bank,35,18+t*16
						MonitorBank bank,"print",st$
						
					Next
	
					For y=0 To 15
						For x=0 To 3
							o=x+(offset+y)/4
							If (o Mod 2)
								Color 0,192,255
							Else
								Color 0,160,255
							EndIf
							Rect 66+x*128,16*y+16,128,16,True
						Next
						For x=0 To 15
							o=(offset+y)*16+x
							If o>=0 And o<size
								If x=mx And y=my And show_highlighter
									Color 255,192,0
									Rect 66+x*32,16+y*16,32,16,True
									
									st$="ADRES: "+o
									PokeShort bank,33,2
									PokeShort bank,35,280
									MonitorBank bank,"print",st$
									PokeShort bank,35,296
									MonitorBank bank,"print","("+Hex$(o)+")"

									PokeShort bank,35,280
									If o<size-1
										st$="SHORT: "+PeekShort(inputbank,o)
										PokeShort bank,33,120
										MonitorBank bank,"print",st$
									EndIf

									If o<size-3
										st$="INT: "+PeekInt(inputbank,o)
										PokeShort bank,33,250
										MonitorBank bank,"print",st$
									EndIf

									If o<size-3
										st$="FLOAT: "+PeekFloat(inputbank,o)
										PokeShort bank,33,410
										MonitorBank bank,"print",st$
									EndIf
								EndIf

								Select hexdec
									Case 0
										st$=Replace$(RSet$(PeekByte(inputbank,o),3)," ","0")
									Case 1
										st$=" "+Right$(Hex$(PeekByte(inputbank,o)),2)
									Case 2
										st$="  "+Chr$(PeekByte(inputbank,o))
								End Select

								PokeShort bank,33,72+x*32
								PokeShort bank,35,18+y*16
								MonitorBank bank,"print",st$

							EndIf
						Next
					Next

				If PeekByte(bank,28) FlipCanvas canvas
			EndIf

		Case "print"

			If dat$="" Return

			vx=PeekShort(bank,33)
			vy=PeekShort(bank,35)

			img=PeekInt(bank,4)
			
			charw=ImageWidth(img)/16
			charh=ImageHeight(img)/16

			For t=0 To Len(dat$)-1
				c=Asc(Mid$(dat$,1+t,1))
				ox=c Mod 16
				oy=c / 16
				DrawImageRect img,vx+t*charw,vy,ox*charw,oy*charh,charw,charh
			Next
			
	End Select

	; bye ^_^
End Function
