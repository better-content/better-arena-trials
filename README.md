# Arena Challenges

# Arena Challenges

Arena Challenges places standalone totems in the overworld. Each totem uses the surrounding natural
terrain for player duels and solo encounters, with three shared one-time rewards and a replay
gallery. Trials provide a solo route to the same reward stock. Duel and trial kits are loaned for
the match and the player's inventory and experience are restored afterward.

The Champion's Blade and Warden's Axe are powerful one-of-a-kind weapons. The Duelist's Sigil
grants Strength II for 30 seconds and recharges after five minutes. A victory earns the right to
claim one reward still available at that arena; replay viewing does not grant rewards.

Arena Challenges stores duel replays through Player Traces in separate append-only files under
`data/player_traces/arena_duels/`, keyed by dimension and arena position. Recordings store motion
only. Player names appear only when both duelists enabled `/arena consent-names` before the duel.
Replays are visual echoes and cannot attack or deal damage.

Player Traces is a required runtime dependency. Totems generate in broad, dry overworld biomes
without changing the surrounding terrain. Fights take place within 48 blocks of the totem.
Players who leave are warned and have 10 seconds to return before forfeiting or ending a trial.
