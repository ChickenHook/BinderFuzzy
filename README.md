# BinderFuzzy

<img src="metadata/android/en-US/images/icon.png" width="150">


An App intended for fuzzing the Binder interface and System Services of Android.
You can use this App for finding bugs and exploits inside the Binder interface or System services at Java level.

# Usage

Binder fuzzy can be used via the python client or via the App itself. Choose one for your needs.

## Using python

WIP

## Using Android App

Prerequisites

```
1. Install the App [Download]()
2. Click 'NEW' to create a new fuzzing action
```

See our video: []()


### The Browser

#### 1. Select System Service to attack

First step is to select a System Service we're going to fuzz. After a click on the "NEW" button the list of available Services appears and you can choose one.

<img src="metadata/android/en-US/images/phoneScreenshots/Screenshot_20200603_181349_org.chickenhook.binderfuzzy.jpg" width="400">

#### 2. Select function or objects to create the call

The next screen lists all members: functions and fields. 
=> If you click on a field the browser will open the object in a new Window.
=> If you click on a method the browser forwards this to the FuzzCreator.

<img src="metadata/android/en-US/images/phoneScreenshots/Screenshot_20200603_181354_org.chickenhook.binderfuzzy.jpg" width="400">

#### 3. Finaly select a method
<img src="metadata/android/en-US/images/phoneScreenshots/Screenshot_20200603_181407_org.chickenhook.binderfuzzy.jpg" width="400">


### Parameter configuration
<img src="metadata/android/en-US/images/phoneScreenshots/Screenshot_20200603_181445_org.chickenhook.binderfuzzy.jpg" width="400">

### Start the test
<img src="metadata/android/en-US/images/phoneScreenshots/Screenshot_20200603_212647_org.chickenhook.binderfuzzy.jpg" width="400">


### Strange findings





