package io.github.mapogolions;

import io.github.mapogolions.model.Book;
import io.github.mapogolions.model.Gender;
import io.github.mapogolions.model.Hero;
import io.github.mapogolions.model.Person;
import java.time.LocalDate;
import static io.github.mapogolions.Db.*;

public class Samples {
    public static void getReferenceOnlyCreatesProxyObject() {
        db(
                atomicBlock(em -> em.getReference(Hero.class, 1L))
        );
    }

    public static void nothingToCommitWhenPersistenceContextIsEmpty() {
        db(
                atomicBlock(
                        em -> em.persist(new Person("Some", "One", Gender.MALE,
                                "someone@gmail.com", LocalDate.of(2020, 02, 15), "Columbia")),
                        em -> em.persist(new Person("John", "Doe", Gender.MALE,
                                "johndoe@gmail.com", LocalDate.of(2020, 02, 15), "USA")),
                        em -> em.clear()
                )
        );
    }

    public static void detachRemoveObjectFromPersistenceContext() {
        db(
                atomicBlock(
                        em -> em.persist(new Book("1a", "who")),
                        em -> em.detach(em.find(Book.class, "1a"))
                )
        );
    }

    public static void removeTransientObjectFromFirstLevelCacheDoesNotPreventInsertion() {
        db(
                atomicBlock(
                        em -> em.persist(new Book("1a", "who")),
                        em -> em.remove(em.find(Book.class, "1a"))
                )
        );
    }

    public static void removeTransientObjectWithNaturalKeyExecutesSelectQuery() {
        db(
                atomicBlock(em -> em.remove(new Book("1a", "who")))
        );
    }

    public static void removeTransientObjectWithSurrogateKeyDoesNothing() {
        db(
                atomicBlock(em -> em.remove(new Hero("true hero")))
        );
    }

    public static void forceExecuteDelayedWriteOperation() {
        db(
                atomicBlock(
                        em -> em.persist(new Person("Some", "One", Gender.MALE,
                                "someone@gmail.com", LocalDate.of(2020, 02, 15), "Columbia")),
                        em -> em.createNativeQuery("select * from person").getResultList(),
                        em -> em.persist(new Person("John", "Doe", Gender.MALE,
                                "johndoe@gmail.com", LocalDate.of(2020, 02, 15), "USA")),
                        em -> em.createNativeQuery("select * from person").getResultList()
                )
        );
    }

    public static void eachSessionHasItsOwnFirstLevelCache() {
        db(
                session(
                        atomicBlock(em -> em.persist(new Hero("true hero"))),
                        atomicBlock(em -> em.find(Hero.class, 1L))
                ),
                session(
                        atomicBlock(em -> em.find(Hero.class, 1L))
                )
        );
    }

    public static void firstLevelCacheHasCrossTransactionNature() {
        db(
                session(
                        atomicBlock(em -> em.persist(new Hero("true hero"))),
                        atomicBlock(em -> em.find(Hero.class, 1L))
                )
        );
    }

    public static void retrievalsFetchEntitiesFromDatabaseIfFirstLevelCacheDoesNotContainEntity() {
        db(
                atomicBlock(em -> em.find(Hero.class, 1L))
        );
    }

    public static void retrievalsFirstOfAllSearchForEntitiesInFirstLevelCache() {
        db(
                atomicBlock(
                        em -> em.persist(new Hero("true hero")),
                        em -> em.find(Hero.class, 1L)
                )
        );
    }

    public static void sequenceGenerationStrategyDelayActualInsertionAsLongAsPossible() {
        db(
                atomicBlock(
                        em -> em.persist(new Person("Some", "One", Gender.MALE,
                                "someone@gmail.com", LocalDate.of(2020, 02, 15), "Columbia")),
                        em -> em.persist(new Person("John", "Doe", Gender.MALE,
                                "johndoe@gmail.com", LocalDate.of(2020, 02, 15), "USA"))
                )
        );
    }

    public static void identityGenerationStrategyInsertsRecordsImmediately() {
        db(
                atomicBlock(
                        em -> em.persist(new Hero("Togo")),
                        em -> em.persist(new Hero("Balto"))
                )
        );
    }

    public static void mergeUpdatesOnlyWhenNecessary() {
        db(
                atomicBlock(
                        em -> em.persist(new Book("1a-2b-3c", "Someone")),
                        em -> em.merge(new Book("1a-2b-3c", "Someone"))
                )
        );
    }

    public static void mergeIsRoundTripOperationWhenNaturalPrimaryKeyIsUsed() {
        db(
                atomicBlock(em -> em.merge(new Book("1a-2b-3c", "Someone")))
        );
    }

    public static void mergeHasOnlyInsertionSemanticWhenSurrogatePrimaryKeyIsUsed() {
        db(
                atomicBlock(em -> em.merge(new Hero("Togo")))
        );
    }

    public static void trackingManagedRecordState() {
        db(
                atomicBlock(
                        em -> {
                            var someone = new Person("Some", "One", Gender.MALE,
                                    "someone@yandex.ru", LocalDate.of(2020, 02, 15), "Columbia");
                            em.persist(someone);
                            someone.email("someone@gmail.com");
                        })
        );
    }
}
