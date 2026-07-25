# Palette's Journal

## 2025-02-14 - Initial setup
**Learning:** Found that compiling screenshot tests with experimental shared transition APIs without proper experimental compiler options throws a compilation error.
**Action:** Always make sure the build handles experimental opt-ins or add `@OptIn(ExperimentalSharedTransitionApi::class)` when necessary, or ensure that compiler args allow them if they are warnings promoted to errors.

## 2025-02-14 - Accidental Destructive Deletion Mitigation
**Learning:** Instantly executing destructive actions like deleting user profiles can lead to user frustration and unintended data loss. Implementing localized confirmation dialogs ensures users confirm intentionality before data is permanently removed.
**Action:** When working on destructive features, always prompt for confirmation with a clear warning dialog, respecting both Hindi and English localizations.
