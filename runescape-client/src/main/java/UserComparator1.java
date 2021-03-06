import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;
import net.runelite.mapping.Implements;
import java.util.Comparator;
import net.runelite.mapping.Export;
@ObfuscatedName("qh")
@Implements("UserComparator1")
public class UserComparator1 implements Comparator {
	@ObfuscatedName("o")
	@Export("reversed")
	final boolean reversed;

	public UserComparator1(boolean var1) {
		this.reversed = var1;
	}

	@ObfuscatedName("o")
	@ObfuscatedSignature(descriptor = "(Lnd;Lnd;I)I", garbageValue = "314043355")
	@Export("compare_bridged")
	int compare_bridged(User var1, User var2) {
		return this.reversed ? var1.compareTo_user(var2) : var2.compareTo_user(var1);
	}

	public boolean equals(Object var1) {
		return super.equals(var1);
	}

	public int compare(Object var1, Object var2) {
		return this.compare_bridged(((User) (var1)), ((User) (var2)));
	}

	@ObfuscatedName("c")
	@ObfuscatedSignature(descriptor = "(I)V", garbageValue = "1852683966")
	public static void method8034() {
		VarpDefinition.VarpDefinition_cached.clear();
	}
}