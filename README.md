# Better Arena Trials

## Scope and authority

This repository owns its mod-specific behavior and authoring inputs. Read [local instructions](AGENTS.md)
and the [shared documentation/policy index](../../better-content-modpack/docs/README.md).


Better Arena Trials places standalone totems in the overworld. Each totem uses the surrounding natural
terrain for player duels and solo encounters, with three shared one-time rewards and a replay
gallery. Trials provide a solo route to the same reward stock. Duel and trial kits are loaned for
the match and the player's inventory and experience are restored afterward.

The Champion's Blade and Warden's Axe are powerful one-of-a-kind weapons. The Duelist's Sigil
grants Strength II for 30 seconds and recharges after five minutes. A victory earns the right to
claim one reward still available at that arena; replay viewing does not grant rewards.

Better Arena Trials stores duel replays through Player Traces in separate append-only files under
`data/better_player_traces/arena_duels/`, keyed by dimension and arena position. Recordings store motion
only. Player names appear only when both duelists enabled `/arena consent-names` before the duel.
Replays are visual echoes and cannot attack or deal damage.

Player Traces is a required runtime dependency. Totems generate in broad, dry overworld biomes
without changing the surrounding terrain. Fights take place within 48 blocks of the totem.
Players who leave are warned and have 10 seconds to return before forfeiting or ending a trial.

When Death's Door is installed, Arena registers a typed read-only life-record eligibility provider.
Active matches and defeated participants awaiting respawn are contained: match activity does not
advance ordinary-world life counters, and contained deaths retain the same world life ID. The
finishing tick remains excluded from collection so the native credited kill for the final trial mob
cannot leak into ordinary-world records after the match is removed. This collection guard does not
contain later ordinary deaths, award records, or change match, kit, damage or reward mechanics.
Source builds stage Death's Door before Player Traces and Arena; no reflection or guessed persistent
player tags are used for this integration.

Separately from life-record eligibility, the existing kit/reward item deferred register is wired
into the mod event bus. Native match-start coverage exposed the previously missing registration;
the repair registers the already-authored Champion's Blade, Warden's Axe and Duelist's Sigil under
their unchanged identifiers. It introduces no new items and changes no kit, reward or match rules.
