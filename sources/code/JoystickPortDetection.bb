max_port = 2
while not keyhit(1)
for test_port = 0 to max_port
if joydown(1,test_port) then
print "joystick detected on port " + str$(test_port)
exit
endif
next
wend
end
