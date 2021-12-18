package net.runelite.client.plugins.hootfighter;

import dev.hoot.api.game.Skills;
import dev.hoot.api.magic.Regular;
import dev.hoot.api.magic.Rune;
import dev.hoot.api.movement.pathfinder.RuneRequirement;
import java.util.Arrays;
import net.runelite.api.Skill;

public enum AlchSpell
{
	HIGH(Regular.HIGH_LEVEL_ALCHEMY, new RuneRequirement(1, Rune.NATURE), new RuneRequirement(5, Rune.FIRE)),
	LOW(Regular.LOW_LEVEL_ALCHEMY, new RuneRequirement(1, Rune.NATURE), new RuneRequirement(1, Rune.FIRE));

	private final Regular spell;
	private final RuneRequirement[] requirements;

	AlchSpell(Regular spell, RuneRequirement... requirements)
	{
		this.spell = spell;
		this.requirements = requirements;
	}

	public Regular getSpell()
	{
		return spell;
	}

	public boolean canCast()
	{
		return Skills.getLevel(Skill.MAGIC) >= spell.getLevel()
			&& Arrays.stream(requirements).allMatch(RuneRequirement::meetsRequirements);
	}
}
