# ADR-0005: deposit フィーチャーフラグの削除

## ステータス

承認済

## 日付

2026-03-22

## 意思決定者

- プロジェクトオーナー

## コンテキストと課題

ADR-0004で `account-creation` フラグの削除をパイロットケースとして実施し、フラグ削除プロセスが確立された。次のステップとして、同じパターンに該当する `deposit` フラグの削除を行う。

- **未使用フラグの残存**: `deposit` フラグは `application.yml` に `true` で定義されているが、`DepositUseCase` では `FeatureFlagService.isEnabled()` によるガードチェックが実装されていない。フラグの値に関わらず入金機能は常に有効であり、フラグ定義自体が死コードとなっている
- **ADR-0004との同一パターン**: `account-creation` フラグと完全に同じ状態（定義あり・コード参照なし）であり、ADR-0004で確立した削除プロセスをそのまま適用できる
- **テストへの影響**: `FeatureFlagServiceImplTest` で `deposit` がテストデータとして使用されている（`isEnabled("deposit")` の結果が `true` であることを検証）
- **フラグ負債の段階的解消**: ADR-0004で述べた5フラグ（`deposit`, `withdrawal`, `transaction-history`, `account-closure`, `withdrawal-fee`）のうち、2番目の削除対象として `deposit` を選定する。最もリスクの低いフラグから順に削除する方針を継続する
- **設定ファイルの信頼性**: `application.yml` に未使用のフラグが残存することで、新規参画者が「このフラグはどこで使われているのか」を調査する無駄な時間が発生する

## 決定

「入金機能のフィーチャーフラグが未使用のまま `application.yml` に残存し死コードとなっている状況において、設定ファイルの正確性と保守性向上という課題に直面し、コードベースの明瞭性とフラグ負債の段階的解消を達成するために、`deposit` フラグ定義の削除と関連テストの修正を採用する。フラグ基盤テストのテストデータ変更に伴う一時的な修正コストを受け入れる。」

### 具体的な決定事項

1. **削除対象**: `application.yml` の `bank.features.deposit: true` 定義を削除する
2. **テスト修正**: `FeatureFlagServiceImplTest` のテストデータから `deposit` を除去し、他の既存フラグ（例: `withdrawal`）をON側のテストデータに置き換える
3. **ユースケース変更なし**: `DepositUseCase` はそもそもフラグを参照していないため、コード変更は不要
4. **他フラグへの影響なし**: 本ADRは `deposit` フラグのみを対象とし、他の4フラグ（`withdrawal`, `transaction-history`, `account-closure`, `withdrawal-fee`）には触れない
5. **ADR-0004プロセスの再利用**: ADR-0004で確立した手順（影響調査 → 定義削除 → テスト修正 → 検証）をそのまま適用する

## 検討した選択肢

### 選択肢1: フラグ定義の削除とテスト修正（採用）

`application.yml` から `deposit` フラグ定義を削除し、関連テストを修正する。

- 良い点:
  - 死コードの除去により `application.yml` の正確性が向上する
  - ADR-0004と同一パターンのため、変更範囲が極めて小さく（設定ファイル1箇所 + テスト1箇所）、リスクが最小限
  - ADR-0004で確立した削除プロセスの再現性を実証できる
  - フラグ負債の段階的解消が前進する
- 悪い点:
  - テストデータの変更により `FeatureFlagServiceImplTest` の意図が若干変わる
  - 将来 `deposit` にフラグ制御を再導入したい場合、フラグ定義の再追加が必要
- リスク:
  - 極めて低い。ユースケースコードに変更がなく、設定値の削除のみのため、機能的な影響は発生しない

### 選択肢2: フラグ定義を残し、DepositUseCase にガードチェックを追加

他のユースケース（`WithdrawUseCase` 等）と同様に、`DepositUseCase` にも `isEnabled("deposit")` のガードチェックを追加し、フラグの一貫性を確保する。

- 良い点:
  - 全ユースケースでフラグパターンが統一され、コードベースの一貫性が向上する
  - 将来 `deposit` を無効化したい場合にフラグで即座に対応できる
- 悪い点:
  - 恒久的にONのフラグに対してガードチェックを追加するのは、無意味な技術的負債の新規追加に等しい
  - フラグ削除の方向性（TBDのライフサイクル原則）に逆行する
  - `DepositUseCase` に不要な `FeatureFlagService` 依存が増え、テストの複雑性が増加する
- リスク:
  - ADR-0004で確立した「不要なフラグは削除する」方針との矛盾が生じ、チーム内の判断基準が曖昧になる

### 選択肢3: 現状維持（フラグ定義をそのまま放置）

`application.yml` の `deposit: true` をそのまま残す。

- 良い点:
  - 作業コストがゼロ
  - 既存テストに一切影響しない
- 悪い点:
  - ADR-0004で「最もリスクの低いフラグから順に削除する」方針を決めたにも関わらず、2番目の削除が進まない
  - 死コードが残存し続け、設定ファイルの信頼性が低下する
  - フラグ削除プロセスの再現性が実証されない
- リスク:
  - ADR-0004がパイロットケースとしての役割を果たさず、フラグ負債の解消が停滞する

## 決定の結果

### 確認方法

1. **フラグ定義の削除確認**: `application.yml` に `deposit` のエントリが存在しないことを確認する
2. **全テストスイートの通過**: `./gradlew test` で全テスト（ユニット・インテグレーション・ArchUnit）がパスすることを確認する
3. **機能の正常動作確認**: フラグ削除後も入金API（`POST /api/v1/accounts/{accountNumber}/deposit`）が正常に動作することを確認する（`DepositUseCase` はフラグ非依存のため影響なしの証明）
4. **grep による残存確認**: `grep -r "deposit" src/main/resources/application.yml` で `deposit` フラグ定義が残っていないことを確認する（ただし `deposit` はドメイン用語としてコード内に正当な参照が存在するため、確認対象は `application.yml` のフラグ定義に限定する）

### 影響

- ポジティブ:
  - `application.yml` から2つ目の死コードフラグが除去され、設定ファイルの正確性がさらに向上する
  - ADR-0004で確立した削除プロセスの再現性が実証され、残りのフラグ削除への信頼性が高まる
  - フラグ負債の段階的解消が着実に前進する
- ネガティブ:
  - `FeatureFlagServiceImplTest` のテストデータ変更が必要（軽微）
  - `deposit` をフラグで制御する柔軟性が失われる（ただし現時点でも制御されていない）

## 追加考慮事項

### ドメインモデルへの影響

- **変更なし**: `DepositUseCase` は `FeatureFlagService` に依存しておらず、ユースケース層・ドメイン層への変更は一切不要。影響は `application.yml`（設定）と `FeatureFlagServiceImplTest`（テストデータ）のみに限定される
- **ドメイン用語との区別**: `deposit` はドメイン用語（入金）としてコードベース全体で使用されている（`Account.deposit()`, `Transaction.deposit()`, `DepositUseCase` 等）。本ADRの削除対象はあくまで `application.yml` のフィーチャーフラグ定義のみであり、ドメインコードには一切影響しない

### TBD（トランクベース開発）への影響

- **フラグ削除プロセスの再現実証**: ADR-0004（パイロット）→ ADR-0005（2件目）により、フラグ削除手順の再現可能性が実証される。これにより、残りの3フラグ（`withdrawal`, `transaction-history`, `account-closure`/`withdrawal-fee`）の削除計画に信頼性が生まれる
- **削除優先度の整理**: 死コードフラグ（コード参照なし）の削除完了後、次のフェーズとしてアクティブフラグ（コード参照あり・恒久ON）の削除に進む。`withdrawal` と `transaction-history` は `isEnabled()` ガードチェックの除去が必要であり、`deposit` より変更範囲が大きい

### 学習目的

- **反復的改善の体験**: 同じパターンの問題を繰り返し解決することで、リファクタリングの「反復的改善」を体験する
- **フラグ削除の判断基準**: 「コードで参照されていないフラグは即座に削除できる」という明確な判断基準の定着
- **段階的リスク管理**: 低リスク（死コード）→ 中リスク（アクティブON）→ 高リスク（アクティブOFF）の順に削除を進める段階的アプローチの学習

## トレーサビリティ

- **関連ADR**:
  - [ADR-0001: 銀行システムサンプルプロジェクトのアーキテクチャ](../0001-bank-system-tbd-sample/01-adr.md)（フィーチャーフラグ運用方針の根拠）
  - [ADR-0004: account-creation フィーチャーフラグの削除](../0004-feature-flag-removal-after-release/01-adr.md)（パイロットケース。本ADRは同一パターンの2件目として、プロセスの再現性を実証する）
- **実装PR**: （実装後に追記）
- **参考資料**:
  - Pete Hodgson, ["Feature Toggles (aka Feature Flags)"](https://martinfowler.com/articles/feature-toggles.html) — リリーストグルのライフサイクル管理とToggle Debt
  - [Trunk Based Development - Feature Flags](https://trunkbaseddevelopment.com/feature-flags/) — フラグの短命性の原則
