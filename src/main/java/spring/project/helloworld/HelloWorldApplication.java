package spring.project.helloworld;

import ch.qos.logback.core.boolex.EvaluationException;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.project.helloworld.entity.Person;
import spring.project.helloworld.entity.Product;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class HelloWorldApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldApplication.class);

    public void reactor() {
        Mono.just(new Person(1, "Nicolas", 26))
                .subscribe(person -> log.info("[reactor] Person: " + person));
    }

    public void rxjava() {
        Observable.just(new Person(1, "Dyana", 25))
                .subscribe(person -> log.info("[rxjava] Person: " + person));
    }

    public void mono() {
        Mono.just(new Person(1, "nicolas", 22))
                .subscribe(person -> log.info(person.toString()));
    }

    public void flux() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));
        people.add(new Person(3, "maria", 23));
        Flux.fromIterable(people).subscribe(person -> log.info(people.toString()));
    }

    public void fluxMono() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));
        people.add(new Person(3, "maria", 23));

        Flux<Person> flux = Flux.fromIterable(people);
        flux.collectList().subscribe(people1 -> log.info(people1.toString()));
    }

    public void range() {
        Flux.range(0, 5).subscribe(value -> log.info("value: " + value));
    }

    public void repeat() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));
        people.add(new Person(3, "maria", 23));

        Flux.fromIterable(people)
                .repeat(1)
                .subscribe(person -> log.info(person.toString()));
    }

    public void map() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));
        people.add(new Person(3, "maria", 23));

        Flux.fromIterable(people)
                .map(person -> {
                    person.age = person.age * 2;
                    return person;
                }).subscribe(person -> log.info(person.toString()));
    }

    public void flatMap() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));
        people.add(new Person(3, "maria", 23));

        Flux.fromIterable(people)
                .flatMap(person -> {
                    person.age = person.age + 2;
                    return Mono.just(person);
                }).subscribe(person -> log.info(person.toString()));
    }

    public void groupBy() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(1, "dyana", 24));
        people.add(new Person(3, "maria", 23));

        Flux.fromIterable(people)
                .groupBy(person -> person.id)
                .flatMap(idFlux -> idFlux.collectList())
                .subscribe(people1 -> log.info(people1.toString()));
    }

    public void filter() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(1, "dyana", 24));
        people.add(new Person(3, "maria", 23));

        Flux.fromIterable(people)
                .filter(person -> person.age > 23)
                .subscribe(person -> log.info(person.toString()));
    }

    public void distinct() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(1, "dyana", 24));
        people.add(new Person(3, "maria", 23));

        Flux.fromIterable(people)
                .distinct(person -> person.id)
                .subscribe(person -> log.info(person.toString()));
    }

    public void merge() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));

        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Coca-Cola", 100));
        products.add(new Product(2, "Pepsi", 100));

        Flux<Person> flux1 = Flux.fromIterable(people);
        Flux<Product> flux2 = Flux.fromIterable(products);

        Flux.merge(flux1, flux2).subscribe(person -> log.info(person.toString()));
    }

    public void zip() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));
        people.add(new Person(3, "maria", 24));

        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Coca-Cola", 100));
        products.add(new Product(2, "Pepsi", 100));

        Flux<Person> flux1 = Flux.fromIterable(people);
        Flux<Product> flux2 = Flux.fromIterable(products);

        Flux.zip(flux1, flux2)
                .subscribe(value -> log.info(value.toString()));
    }

    public void zipWith() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));
        people.add(new Person(3, "maria", 24));

        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Coca-Cola", 100));
        products.add(new Product(2, "Pepsi", 100));

        Flux<Person> flux1 = Flux.fromIterable(people);
        Flux<Product> flux2 = Flux.fromIterable(products);

        flux1.zipWith(flux2, (f1, f2) -> String.format("flux1: %s, flux2: %s", f1, f2))
                .subscribe(value -> log.info(value.toString()));
    }

    public void errorReturn() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 24));
        people.add(new Person(3, "maria", 24));

        Flux.fromIterable(people)
                .concatWith(Flux.error(new RuntimeException("UN ERROR")))
                .onErrorReturn(new Person(0, "lol", 0))
                .subscribe(person -> log.info(person.toString()));
    }

    public void takeUntil() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 23));
        people.add(new Person(3, "maria", 24));

        Flux.fromIterable(people)
                .takeUntil(person -> person.age >= 23)
                .subscribe(person -> log.info(person.toString()));
    }

    public void timeOut() throws InterruptedException {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 23));
        people.add(new Person(3, "maria", 24));

        Flux.fromIterable(people)
                .delayElements(Duration.ofSeconds(1))
                .timeout(Duration.ofSeconds(2))
                .subscribe(person -> log.info(person.toString()));
        Thread.sleep(10000);
    }

    public void average() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 23));
        people.add(new Person(3, "maria", 24));

        Flux.fromIterable(people)
                .collect(Collectors.averagingInt(person -> person.age))
                .subscribe(value -> log.info(value.toString()));
    }

    public void count() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 23));
        people.add(new Person(3, "maria", 24));

        Flux.fromIterable(people)
                .count()
                .subscribe(value -> log.info(value.toString()));
    }

    public void min() {
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, "nicolas", 22));
        people.add(new Person(2, "dyana", 23));
        people.add(new Person(3, "maria", 24));

        Flux.fromIterable(people)
                .collect(Collectors.minBy(Comparator.comparing(person -> person.age)))
                .subscribe(value -> log.info(value.toString()));
    }

    @Override
    public void run(String... args) throws Exception {
        //reactor();
        //rxjava();
        //fluxMono();
        //range();
        //repeat();
        //map();
        //flatMap();
        //groupBy();
        //filter();
        //distinct();
        //merge();
        //zip();
        //zipWith();
        //errorReturn();
        //takeUntil();
        //timeOut();
        //average();
        //count();
        //min();
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
}
