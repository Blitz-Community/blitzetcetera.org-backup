r=Input$("Enter a value for the radius (higher is more accurate, 2000 is best): ")

total=0

For x=-r To r
	For y=-r To r
		If Sqr(x^2+y^2)<r
			total=total+1
		End If
	Next
Next

other#=1/Float(r)^2

pivalue#=other#*total

Print pivalue#

WaitKey

End