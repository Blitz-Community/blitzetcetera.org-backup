Global soundch


Function soundinit()
f=WriteFile("tmpsnd.wav")
For n=1 To 22: Read k: WriteShort f,k:Next
For n=1 To 221: WriteShort f,28837: Next
For n=1 To 220: WriteShort f,36699: Next
CloseFile f

sample=LoadSound ("tmpsnd.wav")
LoopSound sample
soundch=PlaySound (sample)
ChannelPlaying soundch
PauseChannel soundch

DeleteFile "tmpsnd.wav"
Return sample
End Function 

Data 18770,17990,918,0,16727,17750,28006,8308,16,0,1,1,44100,0,22664,1,2,16,24932,24948,882,0


Function sound(frequence,duration)

If frequence<-44000 Then frequence=-44101
ChannelPitch soundch,44100+frequence

ResumeChannel soundch
Delay duration
PauseChannel soundch
End Function 



soundinit

sound(239000,130)
sound(339000,130)
sound(439000,130)
sound(739000,130)
sound(239000,130)
sound(139000,130)