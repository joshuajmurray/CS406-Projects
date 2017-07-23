package com.hfad.workout;

/**
 * Created by Joshua on 7/23/2017.
 */

public class Workout {
    private String name;
    private String description;

    public static final Workout[] workouts = {
            new Workout("The Limb Loosener",
                    "5 Handstand push-ups\n10 1=legged squats\n15 Pullups"),
            new Workout("Core Agony",
                    "100 Pullups\n10 Pushups\n100 situps\n100 squats"),
            new Workout("The Wimp Special",
                    "5 pullups\n10 pushups\n15 squats"),
            new Workout("Strength and Length",
                    "500 meter run\n21 x 1.5 pood kettleball swing\n21 x pullups")
    };

    //each workout has a name and description
    private Workout(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.name;
    }
}
