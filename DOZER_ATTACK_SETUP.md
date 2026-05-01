## GraceMC Dozer Attack Mechanic - Setup Instructions

### What's Been Implemented

1. **New ATTACK State** - Added to `DozerState.java` enum
2. **DozerAttackHandler** - Utility class for triggering instant attacks
3. **HudRenderer Enhancement** - Fullscreen texture rendering + big red "DOZER" text
4. **Sound Support** - Added `dozer_attack` sound event to `sounds.json`
5. **Server-Side Logic** - Updated `DozerManager` with attack state handling

### Key Features

✅ **No GUI classes** - Uses HudRenderCallback only
✅ **Full camera freedom** - No freezing or slowness effects during attack
✅ **Instant appearance/disappearance** - 40 ticks (~2 seconds) display time
✅ **Fullscreen overlay** - Texture covers entire screen
✅ **Red DOZER text** - 5x scaled, red (#FF0000), bottom center
✅ **Synchronized sound** - Scary 0.6 pitch attack sound

### How to Use

#### Trigger an Attack from AI/Code

```java
import com.GraceMC.logic.DozerManager;
import net.minecraft.server.network.ServerPlayerEntity;

// In your AI entity or event handler
ServerPlayerEntity player = ...;
DozerManager.triggerDozerAttack(player);
```

Or use the DozerAttackHandler wrapper:

```java
import com.GraceMC.logic.DozerAttackHandler;

DozerAttackHandler.triggerDozer(player);
```

### Required Texture File

You need to place your `dozer_paper.png` texture at:
```
src/main/resources/assets/gracemc/textures/dozer_paper.png
```

**Important:** The texture file from your download (`C:\Users\Taku\Downloads\dozer\`) should be moved to this location.

The texture will be:
- Stretched to cover the entire screen (fullscreen)
- Displayed for ~2 seconds during attack
- Combined with red "DOZER" text overlay

### Required Sound File

You need to place your `dozer_attack` sound at:
```
src/main/resources/assets/gracemc/sounds/dozer_attack.ogg
```

The sound will:
- Play at volume 1.0f
- Pitch 0.6f (scary low tone)
- Play immediately when attack is triggered

### Technical Details

**Duration:**
- Attack display: 40 ticks (2 seconds at 20 TPS)
- Instant on/off (1 tick each)

**No Performance Penalties:**
- No slowness effects applied during attack
- No camera locking or GUI overlay
- Player can move and rotate freely
- Standard game speed maintained

**Network Sync:**
- Uses Fabric networking to sync state from server to client
- Automatic client-side rendering via HudRenderCallback
- Sound plays on client-side event

### Testing

To test without adding sounds/textures:
1. The code will compile successfully
2. Fullscreen effect will use debug texture (if not found)
3. Text will render correctly even without texture

### Customization

You can modify attack settings in:
- `DozerAttackHandler.java`: Change `ATTACK_DURATION_TICKS` (currently 40)
- `DozerHudRenderer.java`: Adjust text scale, position, color
- `sounds.json`: Change sound properties

### Next Steps

1. Place your texture file: `dozer_paper.png`
2. Place your sound file: `dozer_attack.ogg`
3. Build and run `gradle build`
4. Test with: `/dozer trigger @s` (if command exists)

---

**Version:** Minecraft 1.21.1, Fabric API
**Mod ID:** gracemc
