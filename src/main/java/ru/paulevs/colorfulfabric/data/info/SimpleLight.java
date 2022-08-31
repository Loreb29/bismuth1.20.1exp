package ru.paulevs.colorfulfabric.data.info;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class SimpleLight extends LightInfo {
	private final int[] simple;
	private final int[] advanced;
	
	public SimpleLight(int color, int radius) {
		color = reverse(color);
		
		simple = new int[] {
			color,
			multiply(color, 0.75F),
			multiply(color, 0.29F),
		};
		
		advanced = new int[radius];
		advanced[0] = color;
		for (byte i = 1; i < radius; i++) {
			float power = 1.0F - (float) i / radius;
			advanced[i] = multiply(color, power);
		}
	}
	
	@Override
	public int getSimple(Level level, BlockPos pos, byte i) {
		return i == 0 ? simple[0] : i < 7 ? simple[1] : simple[2];
	}
	
	@Override
	public int getAdvanced(Level level, BlockPos pos, byte i) {
		return advanced[i];
	}
	
	@Override
	public int getRadius() {
		return advanced.length;
	}
}