package eyeq.doubledoor;

import eyeq.doubledoor.event.DoubleDoorEventHandler;
import eyeq.util.common.Utils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static eyeq.doubledoor.DoubleDoor.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
public class DoubleDoor {
    public static final String MOD_ID = "eyeq_doubledoor";

    @Mod.Instance(MOD_ID)
    public static DoubleDoor instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Utils.EVENT_BUS.register(new DoubleDoorEventHandler());
    }
}
