# ZljHttp
一套好用网络框架，RxJava2 + Retrofit2 + OkHttp3 + RxLifecycle2 框架整合封装的网络请求框架

- 基本的get、post、put、delete、4种请求
- 支持tag取消，支持取消全部请求
- 支持绑定组件生命周期自动管理网络请求   
- 支持链式调用
- 支持表单格式，String，json格式数据提交请求
- 基本回调包含 onSuccess、onError、onCancel ...
- 单/多文件上传，多文件情况下，可以区分当前文件下标
- 下载功能，并支持进度回调
- 支持合并请求

## Tip
建议阅读代码，在此基础上改造成适用自己项目的框架

## 使用方式

## 初始化baseUrl
```Java
ZljHttp.Configure.get().baseUrl("https://www.baidu.com");

```

## 一、发起请求

### 1、GET请求
```java
ZljHttpRequest.get()
                .lifecycle(this). //不绑定生命周期，可能会内存泄漏
                .tag(tag)  //设置tag标示，用于取消请求， 如果不设置tag，默认System.currentTimeMillis()
                .addParameter(paramsMap)  //添加请求参数
                //.addHeader(headerMap)   //请求头
                //.baseUrl("")  //不设置则用默认的baseUrl
                .apiUrl(pathUrl) //path，“api/message/get_message_prompt_info”
                //.isMainThread(false) //设置回调在主线程还是子线程，默认是回调到主线程
                .build()
                .execute(new HttpCallback<MessageCenterBean>() {

			@Override
                    public void onSuccess(MessageCenterBean messageCenterBean) {
                    
                     }

                    @Override
                    public void onError(String code, String desc) {
                      
                    }
                    
                     @Override
                    public void onFail(String code, String desc) {
                        
                    }

                    @Override
                    protected void onRequestStart() {
                     }

                    @Override
                    protected void onRequestFinish() {
                    }

                    @Override
                    protected void onNotNet() {
                    }

                    @Override
                    public void onCancel() {
                        
                   }
                });
```

如果HttpCallback没有范型，默认onSuccess的参数，将会是JsonElement，客户端可以根据需求，自行解析

```java
 ...
.execute(new HttpCallback() {
			   @Override
                    public void onSuccess(Object object) {
                    if (object instanceof JsonElement) {
                             Gson gson = new Gson();
                             MessageCenterBean messageCenterBean = gson.fromJson((JsonElement) object, MessageCenterBean.class);
                            }
                     }
                });

```


### 2、POST请求
```java
ZljHttpRequest.post()
                .lifecycle(this) //不绑定生命周期，可能会内存泄漏
                .tag(tag)  //设置tag标示，用于取消请求
                .addHeaderUrlName("home") //该请求是那个域的,home域名或者product域
                .addParameter(paramsMap)  //添加请求参数
                .apiUrl(pathUrl) // “api/message/get_message_prompt_info”
                //.setBodyString("jsonStr",true) //提交Json数据
                .build()
                .execute(new HttpCallback<MessageCenterBean>() {

			@Override
                    public void onSuccess(MessageCenterBean messageCenterBean) {
                    
                     }
                });
```
### 3、上传

#### 支持上传多个文件
```Java
		 //第一种情况：一个key，对应多个文件
        List<File> fileList = new ArrayList<>();


       //第二种情况：多个文件，不同的key值，添加IdentityHashMap中。
        File file = new File("filePath");
        Map<String, File> fileMap = new IdentityHashMap<>();
        fileMap.put(new String("file"),file);
        //fileMap.put(new String("xxx",file2)；

        ZljHttpRequest.upload()
                .tag(tag)
                .lifecycle(this)
                .apiUrl("api/common/upload_file")
                .addParameter(paramsMap)
                .file(fileMap)
                //.file("file", fileList) //一个key，对应多个文件
                .build()
                .execute(new UploadCallback<UploadBean.ItemBean>() {

					/**	
                     * 上传进度回调
                     * @param file 		       当前上传文件
                     * @param currentSize      当前值
                     * @param totalSize        总大小
                     * @param progress         进度
                     * @param currentIndex     当前下标
                     * @param totalFile        总文件数
                     */
                    @Override
                    public void onProgress(File file, long currentSize, long totalSize, float progress, int currentIndex, int totalFile) {
                    
                    }

                    @Override
                    public void onSuccess(UploadBean.ItemBean value) {
                       
                    }

                });

```
### 4、下载

```Java
//文件保存路径
String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+ "img.png");

//下载地址
String imageUrl = “http://cdn.huodao.hk/upload_img/20201106/91bce0bf1a436f3929c8f589aa897521.png”；

 ZljHttpRequest.download()
                        .tag(tag) //可以根据tag，进行取消
                        .apiUrl(imageUrl) //设置请求路径
                        .saveFilePath(filePath)
                        .build()
                        .execute(new DownloadCallback<File>() {

                            @Override
                            public void onProgress(long currentSize, int progress) {
                            
                            }

                            @Override
                            public void onSuccess(File file) {
                               showToastWithShort("图片已保存到手机");
                            }

                        });


```

## 二、合并请求

顾名思义，合并请求指：多个接口的请求结果合并在一起，返回给客户端。

举个例子：2个接口合并请求


```Java
	//请求1
	Observable<String> observable1 = ZljHttpRequest.get()
                .addParameter(params)
                .apiUrl("api/message/get_message_prompt_info")
                .call();

//请求2
        Observable<String> observable2 = ZljHttpRequest.get()
                .apiUrl("api/app/home/activity_info")
                .call();


        ZljHttpRequest.get()
                .lifecycle(this)
                .tag(111)
                .build()
                .zip(observable1, observable2)
                .executeMerge(new MergeHttpCallback<Map<Class<?>, Object>>() {

                                  @MergeType({MessageCenterBean.class, NewAdvertBean.class})
                                  @Override
                                  public void onSuccess(Map<Class<?>, Object> resultMap) {
                                      Logger2.d(TAG, "success");
                                    
                                      NewAdvertBean newAdvertBean = (NewAdvertBean) resultMap.get(NewAdvertBean.class);
                                      MessageCenterBean messageCenterBean = (MessageCenterBean) resultMap.get(MessageCenterBean.class);
                                  }


                                  
                              }

                );
```
注意：在onSuccess方法上添加MergeType注解，MergeType注解的值为数组
@MergeType({MessageCenterBean.class, NewAdvertBean.class})

注解中Class顺序，必须和Observable一一对应哦



## 三、取消请求

### 1. 根据tag取消
```java
ZljHttp.cancel(tag);
```
### 2.取消全部
```java
ZljHttp.cancelAll();
```
### 3.是否取消请求
```Java
ZljHttp.isCanceled(tag);
```
 
 

