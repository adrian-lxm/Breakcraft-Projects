package de.Breakcraft.Challenges.Utils;

import de.Breakcraft.Challenges.Challenges.Challenge;
import de.Breakcraft.Challenges.Challenges.HighFromTheSky;
import de.Breakcraft.Challenges.Challenges.LowLive;
import de.Breakcraft.Challenges.Challenges.NoneArmor;

import java.util.ArrayList;
import java.util.List;

public class ChallengeManager {
    public List<Challenge> challenges = new ArrayList<>();

    public ChallengeManager() {
        new HighFromTheSky(); //HighFromTheSky hat Fehler, muss aber als Constructor gerufen werden für eine static instance
        challenges.add(new NoneArmor());
        challenges.add(new LowLive());
    }

}
