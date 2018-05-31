# SingleClick
## 安卓点击事件防重库

依赖本库，不用写任何代码，所有点击事件自动防重复点击；

对 butterKnife 自动生成的点击事件同样有效

默认防重复点击间隔 500ms

如果想自定义点击事件间隔，加上注解（参数单位ms）：
```
@SingleClick(1000)
public void onClick(View v) {
   ...
}
```

注解参数为0 表示取消防重 不写参数 默认500ms

若在一个点击事件方法有多个view的情况，想排除其中某些view不防双击使用以下方式:
```
    @SingleClick(value = 1000, except = {R.id.tv1, R.id.button})
    @OnClick({R.id.tv1, R.id.button, R.id.button2})
    public void onViewClicked(View view) {
       ...
    }
```

ps：直接在布局里指定的点击事件无法做到自动防重，请打上注解

### 依赖方法:
#### To get a Git project into your build:
#### Step 1. Add the JitPack repository to your build file
1.在全局build里面添加下面github仓库地址

Add it in your root build.gradle at the end of repositories:
```
buildscript {
    ...
    dependencies {
	...
        classpath 'cn.leo.plugin:magic-plugin:1.0.0' //java 用这个  
	classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.0' //kotlin 用这个
    }
}
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
google()和jcenter()这两个仓库一般是默认的，如果没有请加上

#### Step 2. Add the dependency
2.在app的build里面添加插件和依赖
```
...
apply plugin: 'cn.leo.plugin.magic' //java 用这个
apply plugin: 'android-aspectjx'  //kotlin 用这个，编译速度会慢点    
...
dependencies {
	implementation 'com.github.jarryleo:SingleClick:v1.2'
}
```

> 用于支持kotlin的插件用的是 [aspectjx](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx)   
> 感谢插件作者    
> 因为编织所有二进制文件的问题导致编译速度慢的问题，请查看原作者提供的解决方案 
