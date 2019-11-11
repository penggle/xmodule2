package com.penglecode.xmodule.java.reactor;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.LockSupport;

import reactor.core.publisher.Mono;

/**
 * Mono 表示的是包含 0 或者 1 个元素的异步序列。该序列中同样可以包含与 Flux 相同的三种类型的消息通知。
 * Flux 和 Mono 之间可以进行转换。对一个 Flux 序列进行计数操作，得到的结果是一个 Mono<Long>对象。
 * 把两个 Mono 序列合并在一起，得到的是一个 Flux 对象。
 * 
 * 创建 Mono
 * 
 * Mono 的创建方式与之前介绍的 Flux 比较相似。Mono 类中也包含了一些与 Flux 类中相同的静态方法。这些方法包括 just()，empty()，error()和 never()等。除了这些方法之外，Mono 还有一些独有的静态方法。

 * 		fromCallable()、fromCompletionStage()、fromFuture()、fromRunnable()和 fromSupplier()：分别从 Callable、CompletionStage、CompletableFuture、Runnable 和 Supplier 中创建 Mono。
 * 		delay(Duration duration)和 delayMillis(long duration)：创建一个 Mono 序列，在指定的延迟时间之后，产生数字 0 作为唯一值。
 * 		ignoreElements(Publisher<T> source)：创建一个 Mono 序列，忽略作为源的 Publisher 中的所有元素，只产生结束消息。
 * 		justOrEmpty(Optional<? extends T> data)和 justOrEmpty(T data)：从一个 Optional 对象或可能为 null 的对象中创建 Mono。只有 Optional 对象中包含值或对象不为 null 时，Mono 序列才产生对应的元素。
 * 
 * @author 	pengpeng
 * @date	2019年10月12日 上午9:26:39
 */
public class MonoExample {

	public static void just() {
		Mono.just(new Date()).subscribe(System.out::println);
	}
	
	public static void fromRunnable() {
		Mono.fromRunnable(() -> {
			System.out.println("running");
		}).subscribe(System.out::println);
	}
	
	public static void fromCallable() {
		Mono.fromCallable(() -> {
			System.out.println("calling");
			return "called";
		}).subscribe(System.out::println);
	}
	
	public static void fromFuture() {
		Mono.fromFuture(CompletableFuture.completedFuture("completed")).subscribe(System.out::println);
	}
	
	public static void fromSupplier() {
		Mono.fromSupplier(() -> {
			System.out.println("suppling");
			return "supplied";
		}).subscribe(System.out::println);
	}
	
	/**
	 * 创建一个 Mono 序列，在指定的延迟时间之后，产生数字 0 作为唯一值。
	 */
	public static void delay() {
		Thread thread = Thread.currentThread();
		Mono.delay(Duration.ofSeconds(10)).doFinally((type) -> {
			System.out.println("type : " + type);
			LockSupport.unpark(thread);
		}).subscribe(System.out::println);
		System.out.println("main");
		LockSupport.park();
	}
	
	/**
	 * 通过 create()方法来使用 MonoSink 来创建 Mono
	 */
	public static void create() {
		Mono.create((sink) -> {
			sink.success("Hello");
		}).subscribe(System.out::println);
	}
	
	public static void main(String[] args) {
		//just();
		//fromRunnable();
		//fromCallable();
		//fromFuture();
		//fromSupplier();
		//delay();
		create();
	}

}
