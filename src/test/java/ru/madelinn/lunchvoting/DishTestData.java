package ru.madelinn.lunchvoting;

import org.springframework.test.web.servlet.ResultMatcher;
import ru.madelinn.lunchvoting.model.Dish;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static ru.madelinn.lunchvoting.model.AbstractBaseEntity.START_SEQ;
import static ru.madelinn.lunchvoting.web.json.JsonUtil.writeIgnoreProps;

public class DishTestData {

    public static final int TIRAMISU_ID = START_SEQ + 5;
    public static final int CAESAR_ID = START_SEQ + 6;
    public static final int GREEK_SALAD_ID = START_SEQ + 7;
    public static final int TACOS_ID = START_SEQ + 8;
    public static final int FISH_AND_CHIPS_ID = START_SEQ + 9;
    public static final int LASAGNA_ID = START_SEQ + 10;
    public static final int TOM_YUM_ID = START_SEQ + 11;
    public static final int HAMBURGER_ID = START_SEQ + 12;
    public static final int NEAPOLITAN_PIZZA_ID = START_SEQ + 13;

    public static final Dish TIRAMISU = new Dish(TIRAMISU_ID, "Tiramisu", 220);
    public static final Dish CAESAR = new Dish(CAESAR_ID, "Caesar", 490);
    public static final Dish GREEK_SALAD = new Dish(GREEK_SALAD_ID, "Greek salad", 250);
    public static final Dish TACOS = new Dish(TACOS_ID, "Tacos", 200);
    public static final Dish FISH_AND_CHIPS = new Dish(FISH_AND_CHIPS_ID, "Fish and chips", 310);
    public static final Dish LASAGNA = new Dish(LASAGNA_ID, "Lasagna", 550);
    public static final Dish TOM_YUM = new Dish(TOM_YUM_ID, "Tom yum", 470);
    public static final Dish HAMBURGER = new Dish(HAMBURGER_ID, "Hamburger", 300);
    public static final Dish NEAPOLITAN_PIZZA = new Dish(NEAPOLITAN_PIZZA_ID, "Neapolitan pizza", 450);

    public static final List<Dish> PENTHOUSE_DISHES = Arrays.asList(TIRAMISU, CAESAR, NEAPOLITAN_PIZZA);

    public static Dish getCreated(){
        return new Dish(null,"Test dish", 666);
    }

    public static Dish getUpdated(){
        return new Dish(TIRAMISU.getId(), "Tiramisu", 777);
    }

    public static void assertMatch(Dish actual, Dish expected){
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "restaurant");
    }

    public static void assertMatch(Iterable<Dish> actual, Dish... expected){
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Dish> actual, Iterable<Dish> expected){
        assertThat(actual).usingElementComparatorIgnoringFields("restaurant").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Dish... expected) {
        return content().json(writeIgnoreProps(Arrays.asList(expected)));
    }
}
