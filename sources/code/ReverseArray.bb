Const SIZE=8

Dim MyArray(SIZE)


Print "Initial array : "

For i=1 To SIZE

	SeedRnd (MilliSecs())
	
	MyArray(i) = 278  + i
	Print "MyArray(" + i + ")=" + MyArray(i)

Next


IndexDown = 1
IndexUp = SIZE 
While IndexUp >= IndexDown

    Tempo = MyArray(IndexDown)
    MyArray(IndexDown) = MyArray(IndexUp)
    MyArray(IndexUp) = Tempo

    IndexUp = IndexUp - 1
    IndexDown = IndexDown + 1
Wend


Print "Reverse array :"

For i=1 To SIZE

	Print "MyArray(" + i + ")=" + MyArray(i)

Next

Print "Press escape to exit..."

While Not KeyDown(1)
Wend
