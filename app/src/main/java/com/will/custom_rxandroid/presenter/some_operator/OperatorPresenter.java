package com.will.custom_rxandroid.presenter.some_operator;


import com.andview.refreshview.utils.LogUtils;
import com.will.custom_rxandroid.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
import rx.observables.SyncOnSubscribe;
import rx.schedulers.Schedulers;

/**
 * Created by will on 16/9/12.
 */

public class OperatorPresenter extends BasePresenter {
    /**
     * just()方法可以传入一到九个参数，它们会按照传入的参数的顺序来发射它们。
     * just()方法也可以接受列表或数组，就像from()方法，但是它不会迭代列表发射每个值,它将会发射整个列表。
     * 通常，当我们想发射一组已经定义好的值时会用到它。
     * 但是如果我们的函数不是时变性的，我们可以用just来创建一个更有组织性和可测性的代码库。
     */
    public void just() {
        subscription = Observable.just("hello word").subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                LogUtils.e("-----complete");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                LogUtils.e("-----onNext" + s);
            }
        });
    }

    /**
     * from()创建符可以从一个列表/数组来创建Observable,并一个接一个的从列表/数组中发射出来每一个对象。
     * 或者也可以从Java Future类来创建Observable，并发射Future对象的.get()方法返回的结果值。
     * 传入Future作为参数时,我们可以指定一个超时的值。Observable将等待来自Future的结果；
     * 如果在超时之前仍然没有结果返回，Observable将会触发onError()方法通知观察者有错误发生了。
     */
    public void from() {
        List<Integer> datas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            datas.add(i);
        }
        subscription = Observable.from(datas).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                LogUtils.e("------from" + integer);
            }
        });
    }

    /**
     * 需要一个Observable但是什么都不需要发射
     */
    public void empty() {
        Observable.empty();
    }

    /**
     * 构造一个不会发射任何数据但是永远都不会结束的observable
     */
    public void never() {
        Observable.never();
    }

    /**
     * 被订阅者只有被订阅的时候才会发出数据,而不是初始化的时候就发出数据
     * http://www.jianshu.com/p/c83996149f5b
     */
    public void defer() {
        Observable observable = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just("weixinjie");
            }
        });

        //模拟延时订阅
        try {
            Thread.sleep(2000);
            subscription = observable.subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    LogUtils.e(s);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送一个可观测的序列(根据初始值跟数量)
     */
    public void interval() {
        subscription = Observable.range(1, 5).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                LogUtils.e(String.valueOf(integer));
            }
        });
    }

    /**
     * 多次发送同一组数据(repeat(int)中的int值为重复几次,并且没有时间延迟)
     */
    public void repeat() {
        subscription = Observable.range(1, 5).repeat(5, AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                LogUtils.e(String.valueOf(integer));
            }
        });
    }

    /**
     * 同上,不过repeat_when代表的是多次订阅信息
     */
    public void repeat_when() {
        subscription = Observable.range(1, 5).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Void> observable) {
                return Observable.timer(5, TimeUnit.SECONDS);
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                LogUtils.e("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer integer) {
                LogUtils.e(String.valueOf(integer));
            }
        });
    }

    //---------------------------Transforming Observables(以下为变换操作符)--------------------------//

    /**
     * buffer操作符是分批将数据一次性的发送出去,例如下面的例子就会打印出:{1,2,3},{4,5}两组
     * 即将数据分割成data.size/3组(如果不整除则为data.size/3+1)
     */
    public void buffer() {

        subscription = Observable.range(1, 5)
                .buffer(3)
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.e("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        LogUtils.e(String.valueOf(integers));
                    }
                });

        /**
         * 下面的函数没有搞懂,请补充
         */
//        subscription = Observable.range(1, 6).buffer(new Func0<Observable<List<Integer>>>() {
//            @Override
//            public Observable<List<Integer>> call() {
//                List<Integer> data = new ArrayList<Integer>();
//                for (int i = 0; i < 3; i++) {
//                    data.add(i);
//                }
//                return Observable.just(data);
//            }
//        }).subscribe(new Action1<List<Integer>>() {
//            @Override
//            public void call(List<Integer> integers) {
//
//            }
//        });
    }

    /**
     * FlatMap将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并后放进一个单独的Observable
     */
    public void flatmap() {
        subscription = Observable.just(1, 2, 3, 4, 5, 6).flatMap(new Func1<Integer, Observable<String>>() {
            @Override
            public Observable<String> call(Integer integer) {
                return Observable.just(String.valueOf(integer));
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                LogUtils.e("onComplete");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                LogUtils.e(s);
            }
        });
    }

    /**
     * 分组
     */
    public void group_by() {
        subscription = Observable.range(1, 6).groupBy(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer % 2;
            }
        }).subscribe(new Subscriber<GroupedObservable<Integer, Integer>>() {
            @Override
            public void onCompleted() {
                LogUtils.e("大的onCommplete");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(final GroupedObservable<Integer, Integer> integerIntegerGroupedObservable) {
                integerIntegerGroupedObservable.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.e("小的onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        LogUtils.e("group: " + integerIntegerGroupedObservable.getKey() + "number: " + String.valueOf(integer));
                    }
                });
            }
        });
    }

    /**
     * 进行map转化
     */
    public void map() {
        subscription = Observable.just(1, 2, 3, 4, 5).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return String.valueOf("integer: " + integer);
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                LogUtils.e(s);
            }
        });
    }

    /**
     * 可以对数据进行包裹,并且会把上次返回的数据进行发送
     * 例如:下面call函数中 s为上次返回的字符串 s2为当前的字符串
     * 如果Observable.just中写入的是int值,那么我们可以利用这个函数进行求和运算(只是举个例子)
     */
    public void scan() {
        subscription = Observable.just("weixinjie", "zhangrui").scan(new Func2<String, String, String>() {
            @Override
            public String call(String s, String s2) {
                if (s != null)
                    LogUtils.e("-----以前的字符串为" + s);
                if (s2 != null)
                    LogUtils.e("-----当前的字符串为" + s2);
                return s2;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                LogUtils.e(s);
            }
        });
    }

    /**
     * Window is similar to Buffer(没搞懂,请补充)
     */
    public void window() {
        subscription = Observable.interval(1, TimeUnit.SECONDS).take(12)
                .window(3, TimeUnit.SECONDS)
                .subscribe(new Action1<Observable<Long>>() {
                    @Override
                    public void call(Observable<Long> observable) {
                        LogUtils.e("subdivide begin......");
                        observable.subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                LogUtils.e("Next:" + aLong);
                            }
                        });
                    }
                });
    }

    //---------------------------Filtering Observables(以下为过滤操作符)--------------------------//

    /**
     * debounce操作符对源Observable每产生一个结果后，如果在规定的间隔时间内没有别的结果产生，则把这个结果提交给订阅者处理，否则忽略该结果。
     * 值得注意的是，如果源Observable产生的最后一个结果后在规定的时间间隔内调用了onCompleted，那么通过debounce操作符也会把这个结果提交给订阅者。
     */
    public void debounce() {
        subscription = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (subscriber.isUnsubscribed())
                    return;
                for (int i = 0; i < 10; i++) {
                    try {
                        subscriber.onNext(i);
                        Thread.sleep(i * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .debounce(4, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.e("onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        LogUtils.e(String.valueOf(integer));
                    }
                });
    }


}