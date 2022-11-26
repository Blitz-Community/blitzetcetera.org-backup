;===========================================================================

;                      [AU.sft-2005]
;    Если в BlitzPlus надо красиво вывести какие-либо данные, то проще и много удобнее
;   сделать это посредством воистину замечательной команды = HtmlViewRun =.
;   Эта программа ничего полезного не делает и дается только в качестве иллюстрации.
;   необятные возможности гипертекста в ваших руках! (Сам то я не силен в дизайне...).

;============================================================================
win=CreateWindow("BlitzPlus",100,100,245,223,0,1)
btn=createbutton("Числа",172,10,60,25,win,1)
btn2=createbutton("Буквы",172,40,60,25,win,1)
btn3=createbutton("Стиль",172,70,60,25,win,1)
SeedRnd Millisecs()
global as$="КНГЗХВПРЛДЧСМТБ",bs$="кнгзхвпрлдчсмтб",cs$="ууееееааааоооояиииию"
;============================================================================
global html = CreateHtmlView(5,5,162,180,win,1)
HtmlViewGo html,"about:blank"
for t=1 to 4
 Read c$
 d$=d$+c$
next
while HtmlViewStatus(html):wend
HtmlViewRun html,"document.body.scroll='no';document.body.style.cssText='border:none;margin:0,0,0,0';"
HtmlViewRun html,d$
nb():tx()
;============================================================================
Repeat
id = WaitEvent()
If id=$803 Then Exit
if id=$401 then
 if EventSource()=btn Then nb()
 if EventSource()=btn2 Then tx()
 if EventSource()=btn3 Then st()
endif
Forever
end
;============================================================================
function nb()
 for t=0 to 4
  for k=1 to 2
   HtmlViewRun html,"UX.rows["+t+"].cells["+k+"].innerText="+rand(0,200)
  next
 next
end function
;============================================================================
function tx()
for t=0 to 12 step 3
 b$=mid(as$,rand(1,15),1)
 for k=1 to 3
  b$=b$+Mid(cs$,rand(1,20),1)+Mid(bs$,rand(1,15),1)
 Next
 HtmlViewRun html,"UX.cells["+t+"].innerText='"+b$+"'"
next
f$=mid(as$,rand(1,15),1)+Mid(cs$,rand(1,20),1)+Mid(bs$,rand(1,15),1)+"а"
b$=mid(as$,rand(1,15),1)+Mid(cs$,rand(1,20),1)+Mid(bs$,rand(1,15),1)+"у"
HtmlViewRun html,"UX.cells[15].innerHTML='"+f$+" любит<BR>"+b$+" !'"
end function
;============================================================================
function st()
  HtmlViewRun html,"for(i=0; i<3; i++)document.all.tags('COL')[i].style.backgroundColor='threedface'"
  HtmlViewRun html,"UX.cellSpacing=1;UX.borderColorDark='#FFFFFF';UX.borderColorLight='888888'"
  HtmlViewRun html,"UX.style.fontFamily='arial';document.all.tags('COL')[1].style.color='#007700'"
end function
;============================================================================
.mdat
data "document.body.innerHTML='<TABLE ID=UX border bordercolor=#009900 width=162 height=180 STYLE=font:12pt>"
data "<COL BGCOLOR=#FFDEAD STYLE=color:#00008B><COL width=32 BGCOLOR=#555555 STYLE=color:#FFFFFF>"
DATA "<COL width=32 BGCOLOR=#DDDDDD><TR><TD><TH><TH><TR><TD><TH><TH>"
DATA "<TR><TD><TH><TH><TR><TD><TH><TH><TR><TD><TH><TH><TR STYLE=color:#FF0000><TH COLSPAN=3></TABLE>'"
;============================================================================
