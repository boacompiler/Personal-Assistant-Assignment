# Personal Assistant Assignment

The final system can be asked basic questions about an object, it will attempt to identify the object, and fetch information about it from Wikipedia.
The system tries to evaluate contextual cues as to what the object is when the noun phrase is ambiguous. The system can evaluate
'what is' questions in many different forms using natural language processing, for example:

*'What is android?'* is ambiguous.

*'On mobile, what is android?'* returns information about the operating system.

*'what is android in robotics?'* returns information about robots.

*'in science fiction, there are robots called androids, what are they?’* also returns information about robots.

The system also has several other small features including telling jokes and a rudimentary calculator. 
The report goes in to much deeper detail, it was written in conjunction with the project, and was a major part of the assignment. 
It justifys a lot of the choices made, and evaluates the project fully. You can find the report [here](https://github.com/boacompiler/Personal-Assistant-Assignment/blob/master/Report.pdf).

This assignment required a proof of concept more than a polished project, 
and as an unfortunate result the code quality does reflect this in some areas. 
I do not own an android device with GApps at this time, so I am hesitant to edit the project without a good testing environment.

The project was built using android studio. Input uses Google voice recognition, processing uses OpenNLP, output uses Google speech synthesis.
