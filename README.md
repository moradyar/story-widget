## Story-Widget (Player SDK sample)

In this repository has been tried to create a SDK on top of player toolset of Android SDK making play videos piece of cake.

Only add StoryWidget to your xml layout files and then just call play function in your code.

In this repository there has been defined two different widgets:

* StandardStoryWidget
* ExoStoryWidget

### StandardStoryWidget: 

This widget uses standard media player of Android SDK and when you use this widget you **_don't have to_** add ExoPlayer dependencies to your target project. Then this widget is natively developed in Kotlin.

### ExoStoryWidget:

This widget uses ExoPlayer library as the under the hood toolset. Then if you use this type of widget you need to add ExoPlayer dependencies.



#### This SDK is not something to use in your production and it is just a sample SDK development repository.



```
MIT License

Copyright (c) 2021 Mohammad Moradyar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

