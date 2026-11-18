# Rasengan Garcia (school project)

Package: `com.school.rasengangarcia`   Main class: `GarciaActivity`   Screen: portrait

| Setting | Value | Where to change it |
|---|---|---|
| Frame speed | 100 ms (original frames), 40 ms (in-betweens) | `res/drawable/warrior_frames.xml` and `wind_frames.xml` (keep both lists the same) |
| Throw speed | 700 ms | `res/anim/blast_wind.xml` |
| Scene size | 55% of screen width | `res/layout/view_blast.xml` (`layout_constraintWidth_percent`) |
| Zoom | x1.45 | `res/layout/view_blast.xml` (`scaleX`, `scaleY`) |
| Ball size | 0.40 x character | baked into `wind_1..9.png` |
| Voice | pitch 0.90, speed 0.90 | `GarciaActivity` |

Folders (`app/src/main/`):

| Folder | What is inside |
|---|---|
| `java/.../GarciaActivity` | The only code: plays the frames, throws the ball, loops, shouts |
| `res/anim/blast_wind.xml` | Tween animation: moves the ball right and fades it out |
| `res/drawable/` | Frame animations: lists of pictures with a time for each |
| `res/drawable-nodpi/` | The pictures: `warrior_1..9` (character), `wind_1..9` (Rasengan) |
| `res/layout/view_blast.xml` | The screen: two stacked ImageViews |
| `res/raw/` | Put `rasengan_shout.mp3` here for your own voice |

One loop: frames play for about 0.56 s, the ball is thrown the moment the last frame appears
(0.7 s), then it starts again. Odd frames are the original pictures;
even frames are blended in-betweens (delete their lines from both frame lists to remove them).

Buttons: **Start** plays the animation (it loops), **Stop** stops it and returns to the first pose.
The buttons are in `res/layout/view_blast.xml`; their click code is in `GarciaActivity` (`onCreate`).

Sound: `res/raw/rasengan_shout.mp3` (6.4 s) plays when the animation starts. Because it is longer than one
loop (about 1.3 s), it keeps playing while the loop repeats and starts again once it has finished.
Stop cuts it off. To use a different clip, replace that file (keep the name `rasengan_shout`).
