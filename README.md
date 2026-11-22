# DeathSound (Paper 26.3)

Plays a Wither or Warden death sound when a player dies, with adjustable pitch.

## Build with GitHub (no install needed)

1. Create a NEW GitHub repo and upload everything in this folder.
   - `.github` is a hidden folder. On Mac press Cmd+Shift+. in Finder to show it, then drag it in.
   - If it still won't upload: in the repo click Add file > Create new file, type `.github/workflows/build.yml`
     as the name and paste in the contents of `.github/workflows/build.yml` from this zip.
   - Reusing your old DeathBan repo? Replace its files AND its `build.yml` with the ones here,
     because the jar name changed from DeathBan.jar to DeathSound.jar.
2. Open the Actions tab. The build starts by itself (or click Run workflow).
3. When it shows a green check, open the run and download **DeathSound** under Artifacts.
4. Unzip it and put only `DeathSound.jar` in your server's `plugins` folder, then restart.

Needs Paper 26.3+ and Java 25.

## Commands (op only, permission `deathsound.admin`)
- `/deathsound sound <warden|wither>`: choose the death sound
- `/deathsound pitch <0.5-2.0>`: set the pitch (1.0 = normal, lower = deeper)
- `/deathsound test`: hear the current sound and pitch
- `/deathsound reload`: reload config.yml
- `/deathsound`: show current settings

Changes made with commands are saved to `plugins/DeathSound/config.yml`.
