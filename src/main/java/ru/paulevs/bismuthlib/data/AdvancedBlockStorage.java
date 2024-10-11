package ru.paulevs.bismuthlib.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ru.paulevs.bismuthlib.data.info.LightInfo;
import ru.paulevs.bismuthlib.data.transformer.LightTransformer;
import ru.paulevs.bismuthlib.gui.CFOptions;

import java.util.HashMap;
import java.util.Map;

public class AdvancedBlockStorage {
	private static final Direction[] DIRECTIONS = new Direction[] { Direction.UP, Direction.NORTH, Direction.EAST };
	private static final byte[] FLAGS     = new byte[] { 0b001, 0b010, 0b100 };
	private static final byte[] FLAGS_INV = new byte[] { 0b110, 0b101, 0b011 };
	private static final byte MAX = 0b111;
	
	private Map<BlockPos, LightTransformer> transformers = new HashMap<>();
	private Map<BlockPos, LightInfo> lights = new HashMap<>();
	private MutableBlockPos pos = new MutableBlockPos();
	private byte[] storage = new byte[110592];
	private int storedIndex;
	
	public void fill(Level level, int x1, int y1, int z1) {
		storedIndex = 0;
		for (byte dx = 0; dx < 48; dx++) {
			pos.setX(x1 + dx);
			for (byte dy = 0; dy < 48; dy++) {
				pos.setY(y1 + dy);
				for (byte dz = 0; dz < 48; dz++) {
					pos.setZ(z1 + dz);
					storage[storedIndex] = MAX;
					BlockState state = level.getBlockState(pos);
					
					LightInfo light = BlockLights.getLight(state);
					if (light != null) {
						lights.put(pos.immutable(), light);
						storage[storedIndex++] = 0;
						continue;
					}
					
					if (CFOptions.modifyColor()) {
						LightTransformer transformer = BlockLights.getTransformer(state);
						if (transformer != null) {
							transformers.put(pos.immutable(), transformer);
							storage[storedIndex++] = 0;
							continue;
						}
					}
					
					if (blockLight(state, level, pos)) {
						storage[storedIndex] = 0;
					}
					else {
						for (Direction dir: DIRECTIONS) {
							if (blockFace(state, level, pos, dir)) {
								storage[storedIndex] &= FLAGS_INV[dir.getAxis().ordinal()];
							}
						}
					}
					storedIndex++;
				}
			}
		}
		pos.set(x1, y1, z1);
	}
	
	private boolean blockFace(BlockState state, Level level, BlockPos pos, Direction dir) {
		return state.isFaceSturdy(level, pos, dir) || state.isFaceSturdy(level, pos, dir.getOpposite());
	}
	
	private boolean blockLight(BlockState state, Level level, BlockPos pos) {
		return state.isSolidRender(level,pos) || !state.propagatesSkylightDown(level, pos);
	}
}
