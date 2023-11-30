package fr.stonehaven.discord.bot.command;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public abstract class ACooldownCommand extends ListenerAdapter {

    private final long cooldown; // in MS

    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    protected boolean canUse(String userId) {
        if (!cooldowns.containsKey(userId)) return true;
        long lastUsedAt = cooldowns.get(userId);
        return lastUsedAt + cooldown <= System.currentTimeMillis();
    }

    protected void storeUse(String userId) {
        cooldowns.put(userId, System.currentTimeMillis());
    }

}
