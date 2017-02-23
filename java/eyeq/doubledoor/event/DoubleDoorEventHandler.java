package eyeq.doubledoor.event;

import eyeq.util.event.world.BlockDoorOpenedEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DoubleDoorEventHandler {
    @SubscribeEvent
    public void onBlockDoorOpened(BlockDoorOpenedEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        Block block = event.block;
        if(block instanceof BlockDoor) {
            openedBlockDoor(world, pos, state);
            return;
        }
        if(block instanceof BlockTrapDoor) {
            openedBlockTrapDoor(world, pos, state);
            return;
        }
        if(block instanceof BlockFenceGate) {
            openedBlockFenceGate(world, pos, state);
            return;
        }
    }

    private void openedBlockDoor(World world, BlockPos pos, IBlockState state) {
        boolean isRight;
        if(state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER)
            isRight = world.getBlockState(pos.up()).getValue(BlockDoor.HINGE) == BlockDoor.EnumHingePosition.RIGHT;
        else {
            isRight = state.getValue(BlockDoor.HINGE) == BlockDoor.EnumHingePosition.RIGHT;
            state = world.getBlockState(pos.down());
        }
        int coordOffset = isRight ? -1 : 1;

        BlockPos neighborPos;
        switch(state.getValue(BlockDoor.FACING)) {
        case SOUTH:
            neighborPos = pos.add(-coordOffset, 0, 0);
            break;
        case WEST:
            neighborPos = pos.add(0, 0, -coordOffset);
            break;
        case NORTH:
            neighborPos = pos.add(coordOffset, 0, 0);
            break;
        case EAST:
            neighborPos = pos.add(0, 0, coordOffset);
            break;
        default:
            return;
        }
        System.out.println(state.getValue(BlockDoor.FACING));
        IBlockState neighborState = world.getBlockState(neighborPos);
        if(!(neighborState.getBlock() instanceof BlockDoor)) {
            return;
        }

        BlockPos temp = neighborPos;
        if(neighborState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            neighborPos = neighborPos.down();
            neighborState = world.getBlockState(neighborPos);
        }
        Boolean isOpen = state.getValue(BlockDoor.OPEN);
        world.setBlockState(neighborPos, neighborState.withProperty(BlockDoor.OPEN, isOpen), 2);
        world.markBlockRangeForRenderUpdate(temp, neighborPos);
    }

    private void openedBlockTrapDoor(World world, BlockPos pos, IBlockState state) {
        Boolean isOpen = state.getValue(BlockTrapDoor.OPEN);
        for(int i = -1; i < 2; ++i) {
            for(int j = -1; j < 2; j++) {
                if(i == 0 && j == 0) {
                    continue;
                }
                BlockPos neighborPos = pos.add(i, 0, j);
                IBlockState neighborState = world.getBlockState(neighborPos);
                if(!(neighborState.getBlock() instanceof BlockTrapDoor)) {
                    continue;
                }
                world.setBlockState(neighborPos, neighborState.withProperty(BlockTrapDoor.OPEN, isOpen), 2);
                world.markBlockRangeForRenderUpdate(neighborPos, neighborPos);
            }
        }
    }

    private void openedBlockFenceGate(World world, BlockPos pos, IBlockState state) {
        BlockPos[] neighborPoses = new BlockPos[4];
        switch(state.getValue(BlockFenceGate.FACING)) {
        case SOUTH:
        case NORTH:
            neighborPoses[0] = pos.add(-1, 0, 0);
            neighborPoses[1] = pos.add(1, 0, 0);
            break;
        case WEST:
        case EAST:
            neighborPoses[0] = pos.add(0, 0, -1);
            neighborPoses[1] = pos.add(0, 0, 1);
            break;
        default:
            return;
        }
        neighborPoses[2] = pos.add(0, -1, 0);
        neighborPoses[3] = pos.add(0, 1, 0);
        Boolean isOpen = state.getValue(BlockFenceGate.OPEN);
        EnumFacing facing = state.getValue(BlockFenceGate.FACING);
        for(int i = 0; i < 4; i++) {
            BlockPos neighborPos = neighborPoses[i];
            IBlockState neighborState = world.getBlockState(neighborPos);
            if(!(neighborState.getBlock() instanceof BlockFenceGate)) {
                continue;
            }
            world.setBlockState(neighborPos, neighborState.withProperty(BlockFenceGate.OPEN, isOpen).withProperty(BlockFenceGate.FACING, facing), 2);
            world.markBlockRangeForRenderUpdate(neighborPos, neighborPos);
        }
    }
}
