package com.example.service.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.example.service",
    importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
  @ArchTest
  static final ArchRule DOMAIN_IS_FRAMEWORK_FREE =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework..", "org.jooq..", "..http..", "..persistence..");

  @ArchTest
  static final ArchRule APPLICATION_POINTS_INWARD =
      noClasses()
          .that()
          .resideInAPackage("..application..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..http..", "..persistence..", "org.springframework..");

  @ArchTest
  static final ArchRule HTTP_DOES_NOT_USE_JOOQ =
      noClasses()
          .that()
          .resideInAPackage("..http..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.jooq..", "..persistence..repository..", "..persistence..jooq..");

  @ArchTest
  static final ArchRule JOOQ_TYPES_STAY_IN_PERSISTENCE =
      noClasses()
          .that()
          .resideOutsideOfPackages("..persistence..", "..bootstrap..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("org.jooq..");
}
