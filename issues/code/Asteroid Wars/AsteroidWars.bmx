Strict
'AppTitle = "Asteroid Wars Example"

Rem

  —тать€, дл€ которой был написан этот исходник может быть найдена тут:  www.blitzbasic.com - forums - blitzmax - tutorials

EndRem

Include "Ast-TSpaceObject.bmx"
Include "Ast-TShip.bmx"
Include "Ast-TShot.bmx"
Include "Ast-TRock.bmx"

Graphics 800,600,0


Const Intro  = 1
Const Death  = 2
Const Playing = 3

Global Mode = Intro
Global Score, HighestScore
Global Level , Rank$

TRock.Spawn( 50,50, TRock.LARGE )
For Local NR = 1 To 20
 TRock.Spawn( 400,300, TRock.SMALL )
Next

 '√лавный цикл
 Repeat' - - - - - - - - - - - - - - - - - 
  TShot.UpdateAll()
  TRock.UpdateAll()

  Select Mode

   Case Intro

    DrawText "Welcome to Asteroids Wars Beta Version",50,100

    DrawText "Highest Score this run: "+HighestScore+" rocks.",50,130

    DrawText "Steer with LEFT, RIGHT      ",50,170
    DrawText "Thrust with UP and DOWN     ",50,190
    DrawText "Fire with SPACE             ",50,210

    DrawText " ---------  P R E S S   S P A C E   T O  S T A R T  ---------- ",50,260

    If KeyHit(Key_Space) StartGame()

   Case Playing
    DrawText "Score: "+Score, 20, 20
    DrawText "Level: "+level, 20, 40
    TShip.UpdateAll()
    If TShip.List.Count() = 0 
     Mode = Death
     GetRanking()
     Level = 0
     FlushKeys
    EndIf
    If TRock.List.Count() = 0 StartGame()


   Case Death

    DrawText "~~ You Lost Your Ship - G A M E  O V E R ~~ ",50,100

    DrawText "You managed to destroy "+Score+" rocks in "+level+" different zones.",50,130

    DrawText "------------------------  ",50,170
    DrawText " Your RANK : "+Rank$   ,50,190
    DrawText "------------------------  ",50,210

    DrawText " ---------  P R E S S  [R]  T O   R E T R Y  ---------- ",50,260

    If KeyHit(Key_R) Mode = Intro

  EndSelect

 Flip
 Cls
   If KeyHit(Key_Q) End
 Until KeyHit(Key_Escape)'- - - - - - - - 
 End 
 Function StartGame()

  If TRock.List.Count() > 0 'First Time
   'Clear
   For Local Rock:TRock = EachIn TRock.List
    Rock.Destroy()
   Next

   'Start
   TShip.Create()
  Else'In play

   Level:+1
   'Score:+13

  EndIf

  CreateRocks()

  Mode = Playing

 EndFunction

 Function CreateRocks()

  Local Danger = Level*20 + Rand(0,10)
  Local Ship:TShip = TShip( TShip.List.First() )

  Repeat 

   Local X,Y

   Repeat 
    X = Rand(0,800)
    Y = Rand(0,600)
   Until Distance(Ship.X, Ship.Y, X, Y ) > 200

   Select Rand(1,2)
    Case 1
     TRock.Spawn( X, Y, TRock.LARGE )  ;Danger:- 5
    Case 2
     TRock.Spawn( X, Y, TRock.MEDIUM ) ;Danger:- 3
   EndSelect

  Until Danger <= 0

 EndFunction

 Function GetRanking()

  Rank$ = "Classified"
  If Level = 1 Rank = "You didn't get a single one?!" 
  If Level = 1 Rank = "ROFL" 
  If Level = 2 Rank = "LOL"
  If Level = 3 Rank = "Newbie"
  If Level = 4 Rank = "Beginner"
  If Level = 5 Rank = "Average"
  If Level = 6 Rank = "Fair"
  If Level = 7 Rank = "Almost got one"
  If Level = 8 Rank = "Starfighter"
  If Level = 9 Rank = "Rock Destroyer"
  If Level = 10 Rank = "Duplicator"
  '...
  '....

  HighestScore = Score
 EndFunction