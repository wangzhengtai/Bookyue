# Bookyue

### 项目简介

书悦——小说阅读器！

<img src="images/gifhome_640x1137_8s.gif" width="270" height="480">
    <img src="images/gifhome_640x1137_7s.gif" width="270" height="480">
    <img src="images/gifhome_640x1137_9s.gif" width="270" height="480">

此项目尚未完全完成，目前主要只是实现了书籍的阅读功能，其他细节有待后续开发及优化！（数据来源于第三方的api，因为是第三方的api，所以关于网络请求的接口部分就没有上传了）

### 关于小说页面实现的思考

刚开始写的时候，对于小说页面的实现是完全没有头绪的，应该用什么布局和view实现页面的展示？要不要自定义view？以及一章的内容如何分割成多个页面同时完美的衔接在一起？以及如何保存阅读进度？

既然没思路，那就上网找找思路！但是找半天要么不是我想要的，要么是无用的！最后不找了，靠着自己最开始的一点懵懂思路加上网上启发的一些思路，开始慢慢写，最终一点一点实现了想要的效果！

刚开始的时候，肯定是要先实现页面的翻动效果，至于数据显示的先不管了！此时自己感觉肯定要自定义view了，不然不太好实现需求功能！当时自己觉得要写两个自定义view，一个是view继承ViewGroup，用来实现页面的滑动，一个继承View只是单纯的显示文本！

后来自己也是这样做的，但同时也是为了扩展思路，看了两个GitHub的项目，一个是[BookReader](https://github.com/smuyyh/BookReader)，一个是[MONKOVEL](<https://github.com/ZhangQinhao/MONKOVEL>)，主要是大概看了下页面的实现思路，惊奇的发现，BookReader是通过继承View来实现一个自定义View，但这个View封装了所有的逻辑，页面的滑动，事件的相应，数据的展示都是通过一个View，而不是ViewGroup来完成的！既然是一个View，而不是ViewGroup，那么如何切换视图的呢？**最后发现它是通过bitmap来实现，确切的说，是将一页内容绘制到一个bitmap中，然后drawBitmap！切换的时候，就draw另外一个bitmap！**就这个实现确实让我震惊了一把，我擦，还能这么玩？？？是我孤陋寡闻了！MONKOVEL的思路倒是跟我想的是一样的！

最终仔细思考，敲定了思路！

**自定义两个View，一个继承ViewGroup，所有的事件都交给它处理，即[ReadView](view/ReadView)；一个继承View，单纯的绘制文字展示，不处理任何事件，即[PageView](view/PageView)。**

自定义View的好处，此时就显示出来了，拥有极高的可扩展性，小说有着上下翻页，左右翻页，覆盖翻页，以及仿真翻页等不同的滑动效果，通过自定义View都可实现，且可整合到一起，如果是用系统自带的View控件，也可实现某一翻页效果，比如用RecyclerView或者ViewPage，但效果都并不是很好，且单一，毕竟这些不是用来特意为小说阅读实现的控件！还有就是页面内容的展示，此时的系统控件就完全不能符合要求了，唯有自定义控件！

**受网上思路的启发，同时为了避免OOM，ReadView的中只有三个子View，每次往下翻的时候，就将上一页通过removeView()方法移除，同时将当前页变成上一页，下一页变成当前页，同时预先加载下一页，这样保证不会有太多的View存在！同时可以不断的滑动！**

###小说章节内容排版的实现

使用过小说阅读应用的人，都知道页面内容的排版是很工整的，段的开始首行缩进，行与行之间的间隙，段与段之间的间隙，章节标题要加粗，语法规则，首行第一个字符不能是标点符号！以及最重要的页面之间的内容如何链接在一起！

这里特别感谢网上的一篇帖子，[安卓小说阅读器TextView实现思路](https://blog.csdn.net/HelloMyPeople/article/details/84947761)，给自己提供了宝贵的思路！

这里有两个极其重要的方法，**`Panit`的`breakText()`方法用来测量一定距离内所能容纳的字符数，以及`Canvas`的`drawText()`用来绘制文本！**

**实现方案**

* 用`split()`按照换行符将一章内容划分为多段
* 对于每一段，用`breakText()`来不断循测量一行中所能容纳的字符数
* 判断首个字符是不是标点，如果是则起始下标为下一个字符
* 用`drawText()`绘制出一行行字符

这样一来，只需要知道是**第几章的第几段以及段中的起始字符的下标**，便可以开始绘制！同时只需要保存**章节下标，段落下标，起始字符下标**这几项，便可保存小说的阅读进度！

### 关于MVP模式的思考

从最初的MVC模式，了解到MVP模式，MVVM模式！

MVC呢？就是视图、模型、控制器！由于Activity和Fragment不仅起到控制器的作用，还有着视图的逻辑，这样就造成了之间的耦合！所以就有了另外的两种模式！

MVVM呢？依靠动态绑定库实现的模式，给我的感觉就是很流弊，模型与视图相互绑定，视图变化时，能同步到数据上，数据变化时也能同步到视图上，但动态绑定，页是需要付出一些代价的！虽然很流弊，但我一直没用过，不知道怎么应用到项目中，我觉得最适合用MVVM的一个场景就是注册、登录的场景了，用户的输入实时能同步到数据中！

MVP呢？就是**将UI逻辑抽象到View接口层，将业务逻辑抽象到Presenter接口层**！这样一来，视图逻辑和业务逻辑就解耦了，但个人感觉并不能彻底的解耦，因为不管是视图逻辑和业务逻辑都需要**数据**，数据是两者贯通的桥梁！

网上介绍的MVP模式，就是抽取出接口，然后实现接口，这样一来，似乎每个功能模块都需要写好多接口，且还要注意内存泄漏的问题，presenter持有view引用时，应及时释放！感觉自己写的MVP很不好，应该再封装一下，但就自己目前来说，不会！也许应该多看看别人的代码，学习一下别人的程序架构！[MONKOVEL](<https://github.com/ZhangQinhao/MONKOVEL>)好像就是用的MVP模式，当初自己看了一下，它好像有两个超类，一个IPresenter，一个IView，我试着也这样做，但因为一些原因放弃了！一个好的程序架构真的很重要啊！

### 关于单例模式的思考

不知道为何，写程序越来越喜欢用单例了，**如果内存中有某个类的实例存在，且类的基本是不可变的，总觉得应该直接拿内存中的实例直接用，而不是再实例化一个实例！**可这样的话，就只能把类写成单例模式了！

例如，在实现自定义[PageView](View/PageView)的时候，抽取出了一个[DrawTextUtil](Util/DrawTextUtil)工具类，里面封装着绘制所用的画笔，页面边距，字体大小，行间距等诸多成员变量，这些都是绘制小说文本时所必须的，刚开始的时候我是将这些成员都直接放在[PageView](View/PageView)中的，这样的话，每次实例化[ViewPage](View/PageView)的对象的，都需要实例化一堆的成员变量，而且每次实例化ViewPage时，都会重复实例化一堆重复的东西，虽然说removeView的时候，这些就应该会被垃圾回收器回收，但肯定是需要一定时间的，而且既然内存中已存在，且没有变化，为什么要继续重复创建，所以最终将其抽取成一个工具类，使用单例模式！

最早接触到的模式就是单例模式，其中个人最喜欢的还是双重加载模式，从最初的不明白为什么要那样写，到知道为什么要两次判空，为什么要用volatile关键字，还有就是由类加载机制实现的单例模式，但最后了解到枚举单例是最好的，因为可以做到与序列化时仍是单例！

这里就有一个问题了，是否PageView能否重复使用？理论上是可以的，但这样的话，就应该不是重新实例化一个View了，而是拿出原有的View复用了，这样的话，感觉应该如同RecyclerView一般有一个RecyclerPool，但这样的话，肯定实现会很复杂，而且最终效率，不知道会不会提高！这只能算是一个思路！以后有时间，尝试一下吧！

### 关于网络请求的思考

网络请求分为同步请求和异步请求！为了不卡住主线程，所以一般网络请求都是异步的，之后再回调结果！比如OkHttp的异步网络请求方法enqueue()方法返回的response就仍是在异步线程，Retrofit将结果进一步封装，其回调到了主线程，但有时用异步请求就显得很难受，因为有时得到的结果需要进一步处理，然后才能显示在主线程上！

因为Android中网络请求必定不能主线程中进行，所以必须另开线程，所以用网络请求框架的时候，一般使用的都是异步请求方法，这一般来说都是正确的，但如果数据源与多个网络请求有关，即多个数据源全部加载完成后，才能初始化视图，这时候如果每个请求都是异步请求的话，就不太容易控制了，尤其是数据源加载是有顺序的情况的下，异步请求再控制其顺序，代码逻辑就会很混乱，不如自己开一个线程，然后再其中同步全部所以需要的网络请求，然后转回主线程，同步视图！

### 关于数据与视图的思考

现在的应用基本上就是获取数据与展示视图这两大部分，但是数据与视图之间有一个先后顺序，一般都是先获取数据，然后再在视图上展示数据，就是因为这个顺序的原因，出现了**同步**的问题，必须先获取到数据才能展示页面，或者页面更新，必须同步数据，很多问题，就是这样来的！

同时这一次的项目的数据跟以往有着很大不同，原来获取数据的时候就是直接将数据读进到内存中，然后直接就显示出来了，但有的小说很大，十几兆，而且也不已应该全部都加载到内存中，因为每次手机上展示的时候就是一页的内容！但如何让章节数据完美的连接在一起，就是如何让一章内容数据读完的时候，获取到下一章数据，最后就是**在内存中只加载三章的数据，读完一章加载一章！**

### 对自定义View的思考

说实话，自己一般是不愿意自定义View的，为什么？**因为它难！**对于自己这个菜逼来说，能用系统控件达到要求的话，就用系统控件，绝不多余找事！而且经过自己尝试，一般自己写的控件都是有很大问题的，且达到的效果绝对没有系统提供的好，而且Android的事件机制，并不只是dispatchTouchEvent，onInterceptTouchEvent，onTouchEvent这三个方法，中间有很多弯弯绕绕，不明白的话，一踩一个坑！！！但有些时候不得不自定义View，比如此刻，虽然自定义View很麻烦，但一旦完美实现的话，就会发现所有的付出都是值得的！

### 踩的坑

- **Scoller()移动的是View中的内容**

  小说当前的翻页模式是覆盖翻页的，如图！

  <img src="images/S90809-132950.jpg" width="270" height="480">

  我是自定义一个View让其继承ViewGroup，在里面加载了三个子View，布局方面让其覆盖式放置，想通子View的scrollerTo()来达到滚动的效果！因为**scrollerTo()滚动的只是视图，并没有移动View！scrollerTo()滚动的只是视图，并没有移动View！scrollerTo()滚动的只是视图，并没有移动View！**

  重要的话，说三遍！

  但我却没看到，我想要的效果！！！明明水平式放置，同时调用父元素的scroller()方法，就能达到效果！

  因为我给子View加了颜色。。。。。。。

  调用子View的scroller()方法，只是让子View中的视图跑到别的地方去了，但子View本身**还在那，还在那，还在那**，就在屏幕上！但下一页是怎么显示出来的？因为**子View是透明的，透明的，透明的**！所以移走顶层View的视图后，就能看见中间一层View的内容了！

  所以加了颜色后，啥效果都没有！本来给子View加颜色，只是为了便于观察，但没想到把自己给坑了！

  **解决办法**

  发现问题后，就很好解决了，子View最外层用来一个LinearLayout包裹起来，LinearLayout没有甚至任何背景，即透明的，最终达到了想要的效果！

  在网上找解决办法的时候，发现也可以通过drawBitmap()来达到类似的效果！

- **CountDownLatch的错误使用**

  主要就是用这个CountDownLatch来实现线程的等待，在资源加载完成后，才能继续向下执行！但在使用的过程是，却发生了一点问题！

  主要就是在小说翻页的过程中，翻到一章内容的最后一页时，这时需要加载下一章数据，同时返回一个新建的View，会调用createNextView()方法，在这个方法里需要加载数据，由于可能需要走网络请求，所以把加载数据的任务放在了子线程中执行，在线程执行完后，通过runOnUiThread()方法回调到主线程创建View()，然后在View创建完成之后createNextView()方法才能返回，不然就有可能返回一个null或者造成其他别的后果，所以在createNextView()里创建了一个CountDownLatch()对象，将count设置为1，然后在createNextView()最后await()，在runOnUiThread()里创建完View后countDown！

  嗯！然后？然后我就被卡的死死的！一翻到一章的最后一页就被卡死！

  为什么？

  因为createNextView()时运行在主线程的啊，runOnUiThread()也是运行在主线程的，在主线程把自己卡住了，然后还想通过主线程帮自己解锁？？？告诉你，门都没有！！！所以只能被卡死！

  解决办法就是把countDowm放在子线程里执行！但这时突然发现了一个问题，在把创建View()的代码也直接放在子线程里居然也行！可能也许只是更新视图需要放在主线程中？

  其实也可以用thread.join()方法阻塞的，但我最初的时候是用的RxJava切换的线程，然后发现观察者接受不到被观察者发送来的信号，其实是一样的原因，在同一个线程里await()和countDown()，但是当时把方法封装了一下，使得线程切换更隐秘，而且自己完全没想到那方面去，只是一个劲的在想为啥接受不到？直到换了一种方式，才渐渐明白问题出现在哪里！

- **点击文本时出现空白界面**

  在阅读界面，点击中间时，会弹出上下弹窗，但在弹窗弹窗后，发现页面中绘制的字体都没了，只有背景图！

  后来发现是点击的时候，弹出弹窗的过程中，View发生了变化，导致重新调用了View的onDraw()方法，在PageView里有个mY和mLastY，分别表示当前绘制的字符的Y的坐标，以及所允许绘制的最低Y的坐标，也就是说如果mY>mLastY，就表示一页内容绘制完了，不允许再绘制了！

  结果在初始绘制了一遍之后，mY的值早已改变，超过了mLastY，所以再调用，绘制不出来任何东西，同时View中段的下标，起始字符下标都已改变！

  解决办法就是在onDraw()方法中重置需要的数据！但还是不太明白onDraw()究竟是怎么被触发的！

### 待解决问题

* **Retrofit连续两次请求会得到内容为null的情况**

  如何一次请求完之后，就立即再次请求，就会有时得到内容为null的情况，然后就有可能导致出现一些问题，网上说是因为流读取完了的原因，所以才会得到null的内容，不太了解，以后研究！

* **标点符号问题**

  由于标点不能出现在行首，所以将其移到了上一行尾部，由于有着左右内间距的，而且比标点字符是紧挨着上一个字符的，所以如果单纯的硬添一个标点字符是没什么问题的，如下图！注意行尾！

  <img src="images/S90716-162856.jpg" width="270" height="480">

  但如果是多个连在一起的字符，比如……或者。”这样的，硬塞给上一行就极其别扭了，就如同下图这样！

  <img src="images/S90716-162930.jpg" width="270" height="480">

  <img src="images/S90716-163034.jpg" width="270" height="480">

  <img src="images/S90716-163053.jpg" width="270" height="480">

  前移？挪出空间来？可是字符都是一个一个的从头开始绘制的，从哪里挤出地方给多出来的标点？

  观察了一下QQ浏览器上小说书架的部分，发现其每行都是十六个字符，而且标点不会出现在行首，但仔细观察发现虽然每行都是十六个字符，但行与行之间的字符是不对齐的，我的页面上的字符都是都是正对齐的，而QQ浏览器的书架页面有时会出现一个字符是在上一行的两个字符之间的情况，即偏移了！我有理由怀疑，它是将字符与字符之间的间距给挤出来让给了行尾标点！

  <img src="images/S90716-170537.jpg" width="270" height="480">

  呃，虽然我知道字符与字符之间是有间隔的？但还是不知道怎么挤的？或者还有别的办法？

* **快速点击翻页时页面会停留在中间**

  如果在快速的点击翻页的话，就会出现之前页面停留在之间的问题！如下图！

  <img src="images/gifhome_640x1137_5s.gif" width="270" height="480">

* **页面左右内边距的问题**

  左右内边距目前是不对称的，右边明显比左边大一下，主要是由于初始时，我直接将左右内间距设置成了20dp，然后剩下的距离就直接当成绘制的区域了，很明显这个区域最后一部分不能容下一个当前的字符，所以就被空出来了，造成左右边距不对成的情况！

  解决思路，应该先测量当前屏幕的宽度所能最多容纳的字符数，然后得到恰好能容纳整数倍字符的区域，然后将剩下的空间再二等分，作为内边距！简单说，就是要先计算！

* **状态栏的问题**

* **加载慢的问题**

* **无网络时的处理**

* **意外情况下的状态保存**

### 第三方开源库

* Glide
* Retrofit
* Rxjava

### 参考链接

[安卓小说阅读器TextView实现思路](https://blog.csdn.net/HelloMyPeople/article/details/84947761)

[HenCoder Android 开发进阶：自定义 View 1-3 drawText() 文字的绘制](https://hencoder.com/ui-1-3/)

[Android界面开发：自定义View实践之绘制篇](https://juejin.im/post/59cc52ae6fb9a00a3b3c1c61#heading-0)

[Android Scroller完全解析，关于Scroller你所需知道的一切](https://blog.csdn.net/guolin_blog/article/details/48719871)

[android小说阅读器智能断章功能的实现](https://blog.csdn.net/ProgramChangesWorld/article/details/47209475)

[MONKOVEL-安卓端免费的小说阅读器 源码分享](https://blog.csdn.net/github_38075367/article/details/77075477)

[基于Android小说阅读器滑动效果的一种实现](https://blog.csdn.net/freesonhp/article/details/38237997)

[Android自定义View——从零开始实现书籍翻页效果（一）](https://juejin.im/post/5a3215c96fb9a045186ac0fe)
