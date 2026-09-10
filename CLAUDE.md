# FTC SolversLib Codebase

FTC robotics codebase built with Android Studio and SolversLib. Follow these rules when writing, modifying, reviewing, or refactoring code.

## Units

- Distance: inches
- Angle: radians

## Code Design

- Keep code modular and reusable; avoid unnecessary duplication.
- Follow existing codebase patterns before introducing new ones.
- Prefer simple, readable implementations.
- Public static constants (servo positions, motor limits, drive speeds, etc.) belong in `Constants`.
- Generic-purpose math utilities belong in `MathFunctions`, not inside individual subsystems, unless the logic is specific to that subsystem.

## Naming Conventions

| Kind | Style | Example |
|---|---|---|
| Classes | PascalCase | `MathFunctions`, `BlueCloseAuto` |
| Methods | camelCase | `setPower()`, `runOpMode()` |
| Non-static fields | camelCase | `power`, `targetPosition` |
| Static constants | UPPER_SNAKE_CASE | `MAX_POWER`, `DRIVE_SPEED` |

## Conditionals

Always use braces — never one-line conditionals.

```java
// Do
if (condition) {
    doSomething();
}

// Don't
if (condition) doSomething();
```

## Comments

- Use `//` line comments; no block comments for normal code.
- No AI-style section/banner comments.
- Don't comment on what obvious code already shows — prefer readable code over narration.

## Javadocs

**Methods** — never one-liners. Description first, then `@param`/`@return` as applicable:

```java
/**
 * Sets the target power of the motor.
 *
 * @param power target motor power
 */
public void setPower(double power) {
    // ...
}
```

**Classes** — keep concise. Cover purpose, functionality, hardware controlled (if any), intended usage, and `{@link}` to important methods. Skip obvious framework details (e.g. "the constructor initializes fields," "`periodic()` is called by the CommandScheduler").

No example code in Javadocs.

## Codebase Structure

Main source: `TeamCode/src/main`. Layout can shift year to year, but is generally:

- **`globals/`** — globally accessible objects/values, including anything `Robot` exposes.
- **`commandbase/`** — SolversLib subsystems and commands. `Robot` itself does not belong here.
- **`opmode/`** — Autonomous and TeleOp OpModes.
- **`tuning/`** — tuner classes, tuner-creation instructions, and tools for testing individual subsystems/mechanisms.
- **`test/`** (JUnit) — tests for logic, methods, and other testable functionality.
- **Vendored libraries** (e.g. SolversLib) are kept as local copies so the team can modify them when needed. Prefer existing library functionality first, follow its existing patterns, and keep any modifications intentional and documented.

## Git Workflow

- **Build before pushing** (Android Studio: `TeamCode -> build`). Never push code that hasn't built successfully.
- **Push from your personal GitHub account** — never a school or other account that may lack repo access.
- **JUnit tests**: run affected tests before pushing logic changes; add new tests for new logic. Run these manually for now — no CI is configured to run them automatically yet.
- **Pulling**: stash local changes, pull, then restore the stash — never pull in a way that risks overwriting local work.
- **Merges/rebases**: inspect every conflict carefully; never blindly accept one side. Ask a teammate if you don't understand what's happening.
- **Never force-push or rewrite history.** Stop and ask a teammate first if you think it's necessary.

## Before Completing a Change

- [ ] Follows naming conventions
- [ ] Constants live in `Constants`; generic math lives in `MathFunctions`
- [ ] Distances in inches, angles in radians
- [ ] No one-line conditionals
- [ ] Comments and Javadocs follow the standards above
- [ ] Relevant JUnit tests pass (new tests added where applicable)
- [ ] `TeamCode` builds successfully
- [ ] Git diff reviewed before committing/pushing
- [ ] No force push

If you're unsure about a Git operation, merge, rebase, hardware behavior, or architectural decision — ask instead of guessing.
