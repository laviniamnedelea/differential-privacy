//
// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.google.privacy.differentialprivacy;

import static com.google.common.truth.Truth.assertThat;
import static com.google.differentialprivacy.SummaryOuterClass.MechanismType.LAPLACE;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.math.Stats;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LaplaceNoiseTest {
  private static final LaplaceNoise NOISE = new LaplaceNoise();
  private static final int NUM_SAMPLES = 100000;
  private static final double LN_3 = Math.log(3);
  private static final double DEFAULT_MEAN = 0.0;
  private static final double DEFAULT_EPSILON = LN_3;
  private static final int DEFAULT_L_0_SENSITIVITY = 1;
  private static final double DEFAULT_L_INF_SENSITIVITY = 1.0;

  @Test
  public void addNoise_hasAccurateStatisticalProperties() {
    ImmutableList.Builder<Double> samples = ImmutableList.builder();
    for (int i = 0; i < NUM_SAMPLES; i++) {
      samples.add(
          NOISE.addNoise(
              DEFAULT_MEAN,
              DEFAULT_L_0_SENSITIVITY,
              DEFAULT_L_INF_SENSITIVITY,
              DEFAULT_EPSILON,
              /* delta */ null));
    }
    Stats stats = Stats.of(samples.build());

    double variance = 2.0 / (LN_3 * LN_3);
    // The tolerance is chosen according to the 99.9995% quantile of the anticipated distributions
    // of the sample mean and variance. Thus, the test falsely rejects with a probability of 10^-5.
    double sampleMeanTolerance = 4.41717 * Math.sqrt(variance / NUM_SAMPLES);
    double sampleVarianceTolerance = 4.41717 * Math.sqrt(5.0 * variance * variance / NUM_SAMPLES);
    assertThat(stats.mean()).isWithin(sampleMeanTolerance).of(DEFAULT_MEAN);
    assertThat(stats.populationVariance()).isWithin(sampleVarianceTolerance).of(variance);
  }

  @Test
  public void addNoise_differentMean_hasAccurateStatisticalProperties() {
    ImmutableList.Builder<Double> samples = ImmutableList.builder();
    for (int i = 0; i < NUM_SAMPLES; i++) {
      samples.add(
          NOISE.addNoise(
              /* mean */ 42.0,
              DEFAULT_L_0_SENSITIVITY,
              DEFAULT_L_INF_SENSITIVITY,
              DEFAULT_EPSILON,
              /* delta */ null));
    }
    Stats stats = Stats.of(samples.build());

    double mean = 42.0;
    double variance = 2.0 / (LN_3 * LN_3);
    // The tolerance is chosen according to the 99.9995% quantile of the anticipated distributions
    // of the sample mean and variance. Thus, the test falsely rejects with a probability of 10^-5.
    double sampleMeanTolerance = 4.41717 * Math.sqrt(variance / NUM_SAMPLES);
    double sampleVarianceTolerance = 4.41717 * Math.sqrt(5.0 * variance * variance / NUM_SAMPLES);
    assertThat(stats.mean()).isWithin(sampleMeanTolerance).of(mean);
    assertThat(stats.populationVariance()).isWithin(sampleVarianceTolerance).of(variance);
  }

  @Test
  public void addNoise_differentSensitivity_hasAccurateStatisticalProperties() {
    ImmutableList.Builder<Double> samples = ImmutableList.builder();
    for (int i = 0; i < NUM_SAMPLES; i++) {
      samples.add(
          NOISE.addNoise(
              DEFAULT_MEAN,
              DEFAULT_L_0_SENSITIVITY,
              /* lInfSensitivity */ 0.5,
              DEFAULT_EPSILON,
              /* delta */ null));
    }
    Stats stats = Stats.of(samples.build());

    double variance = 2.0 / (4.0 * LN_3 * LN_3);
    // The tolerance is chosen according to the 99.9995% quantile of the anticipated distributions
    // of the sample mean and variance. Thus, the test falsely rejects with a probability of 10^-5.
    double sampleMeanTolerance = 4.41717 * Math.sqrt(variance / NUM_SAMPLES);
    double sampleVarianceTolerance = 4.41717 * Math.sqrt(5.0 * variance * variance / NUM_SAMPLES);
    assertThat(stats.mean()).isWithin(sampleMeanTolerance).of(DEFAULT_MEAN);
    assertThat(stats.populationVariance()).isWithin(sampleVarianceTolerance).of(variance);
  }

  @Test
  public void addNoise_differentEpsilon_hasAccurateStatisticalProperties() {
    ImmutableList.Builder<Double> samples = ImmutableList.builder();
    for (int i = 0; i < NUM_SAMPLES; i++) {
      samples.add(
          NOISE.addNoise(
              DEFAULT_MEAN,
              DEFAULT_L_0_SENSITIVITY,
              DEFAULT_L_INF_SENSITIVITY,
              /* epsilon */ 2 * LN_3,
              /* delta */ null));
    }
    Stats stats = Stats.of(samples.build());

    double variance = 2.0 / (4.0 * LN_3 * LN_3);
    // The tolerance is chosen according to the 99.9995% quantile of the anticipated distributions
    // of the sample mean and variance. Thus, the test falsely rejects with a probability of 10^-5.
    double sampleMeanTolerance = 4.41717 * Math.sqrt(variance / NUM_SAMPLES);
    double sampleVarianceTolerance = 4.41717 * Math.sqrt(5.0 * variance * variance / NUM_SAMPLES);
    assertThat(stats.mean()).isWithin(sampleMeanTolerance).of(DEFAULT_MEAN);
    assertThat(stats.populationVariance()).isWithin(sampleVarianceTolerance).of(variance);
  }

  @Test
  public void addNoise_integralMean_hasAccurateStatisticalProperties() {
    ImmutableList.Builder<Long> samples = ImmutableList.builder();
    for (int i = 0; i < NUM_SAMPLES; i++) {
      samples.add(
          NOISE.addNoise(
              /* mean */ 0L,
              DEFAULT_L_0_SENSITIVITY,
              /* lInfSensitivity */ 1L,
              DEFAULT_EPSILON,
              /* delta */ null));
    }
    Stats stats = Stats.of(samples.build());

    double mean = 0.0;
    double approxVariance = 1.75;
    // The tolerance is chosen according to the 99.9995% quantile of the anticipated distributions
    // of the sample mean and variance. Thus, the test falsely rejects with a probability of 10^-5.
    double sampleMeanTolerance = 4.41717 * Math.sqrt(approxVariance / NUM_SAMPLES);
    assertThat(stats.mean()).isWithin(sampleMeanTolerance).of(mean);
    // Not testing for the variance because it is not clear what variance should be expected.
  }

  @Test
  public void addNoise_epsilonTooSmall_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                DEFAULT_L_0_SENSITIVITY,
                DEFAULT_L_INF_SENSITIVITY,
                /* epsilon */ 1.0 / (1L << 51),
                /* delta */ null));
  }

  @Test
  public void addNoise_epsilonPosInfinity_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                DEFAULT_L_0_SENSITIVITY,
                DEFAULT_L_INF_SENSITIVITY,
                /* epsilon */ Double.POSITIVE_INFINITY,
                /* delta */ null));
  }

  @Test
  public void addNoise_epsilonNan_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                DEFAULT_L_0_SENSITIVITY,
                DEFAULT_L_INF_SENSITIVITY,
                /* epsilon */ Double.NaN,
                /* delta */ null));
  }

  @Test
  public void addNoise_deltaNonnul_throwsEsception() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                DEFAULT_L_0_SENSITIVITY,
                DEFAULT_L_INF_SENSITIVITY,
                DEFAULT_EPSILON,
                /* delta */ 0.0));
  }

  @Test
  public void addNoise_lInfSensitivityTooHigh_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                DEFAULT_L_0_SENSITIVITY,
                /* lInfSensitvity */ Double.MAX_VALUE,
                DEFAULT_EPSILON,
                /* delta */ null));
  }

  @Test
  public void addNoise_lInfSensitivityNan_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                DEFAULT_L_0_SENSITIVITY,
                /* lInfSensitvity */ Double.NaN,
                /* epsilon */ 1,
                /* delta */ null));
  }

  @Test
  public void addNoise_lInfSensitivityNegative_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                DEFAULT_L_0_SENSITIVITY,
                /* lInfSensitivity */ -1.0,
                DEFAULT_EPSILON,
                /* delta */ null));
  }

  @Test
  public void addNoise_lInfSensitivityZero_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                DEFAULT_L_0_SENSITIVITY,
                /* lInfSensitivity */ 0.0,
                DEFAULT_EPSILON,
                /* delta */ null));
  }

  @Test
  public void addNoise_l0SensitivityNegative_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                /* l0Sensitivity */ -1,
                DEFAULT_L_INF_SENSITIVITY,
                DEFAULT_EPSILON,
                /* delta */ null));
  }

  @Test
  public void addNoise_l0SensitivityZero_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN,
                /* lInfSensitivity */ 0,
                DEFAULT_L_INF_SENSITIVITY,
                DEFAULT_EPSILON,
                /* delta */ null));
  }

  @Test
  public void addNoise_l1SensitivityNegative_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN, /* l1Sensitivity */ -1.0, DEFAULT_EPSILON, /* delta */ null));
  }

  @Test
  public void addNoise_l1SensitivityZero_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN, /* l1Sensitivity */ 0.0, DEFAULT_EPSILON, /* delta */ null));
  }

  @Test
  public void addNoise_l1SensitivityNan_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            NOISE.addNoise(
                DEFAULT_MEAN, /* l1Sensitivity */ Double.NaN, DEFAULT_EPSILON, /* delta */ null));
  }

  @Test
  public void sampleGeometric_lowSuccessProbability_hasAccurateStatisticalProperties() {
    ImmutableList.Builder<Double> samples = ImmutableList.builder();
    for (int i = 0; i < NUM_SAMPLES; i++) {
      // Samples are drawn from a geometric distribution with success probability of p = 10^-6.
      samples.add((double) NOISE.sampleGeometric(Math.log(1000000.0 / 999999.0)));
    }
    Stats stats = Stats.of(samples.build());

    double mean = 1000000.0;
    double variance = 1E12;
    // The tolerance is chosen according to the 99.9995% quantile of the anticipated distributions
    // of the sample mean and variance. Thus, the test falsely rejects with a probability of 10^-5.
    double sampleMeanTolerance = 4.41717 * Math.sqrt(variance / NUM_SAMPLES);
    double sampleVarianceTolerance = 4.41717 * Math.sqrt(8E24 / NUM_SAMPLES);
    assertThat(stats.mean()).isWithin(sampleMeanTolerance).of(mean);
    assertThat(stats.populationVariance()).isWithin(sampleVarianceTolerance).of(variance);
  }

  @Test
  public void sampleGeometric_highSuccessProbability_hasAccurateStatisticalProperties() {
    ImmutableList.Builder<Double> samples = ImmutableList.builder();
    for (int i = 0; i < NUM_SAMPLES; i++) {
      // Samples are drawn from a geometric distribution with success probability of p = 0.5.
      samples.add((double) NOISE.sampleGeometric(Math.log(2.0)));
    }
    Stats stats = Stats.of(samples.build());

    double mean = 2.0;
    double variance = 2.0;
    // The tolerance is chosen according to the 99.9995% quantile of the anticipated distributions
    // of the sample mean and variance. Thus, the test falsely rejects with a probability of 10^-5.
    double sampleMeanTolerance = 4.41717 * Math.sqrt(variance / NUM_SAMPLES);
    double sampleVarianceTolerance = 4.41717 * Math.sqrt(34.0 / NUM_SAMPLES);
    assertThat(stats.mean()).isWithin(sampleMeanTolerance).of(mean);
    assertThat(stats.populationVariance()).isWithin(sampleVarianceTolerance).of(variance);
  }

  @Test
  public void sampleGeometric_tooSmallLambda_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> NOISE.sampleGeometric(1.0 / (1L << 59)));
  }

  @Test
  public void getMechanismType_returnsGaussian() {
    assertThat(NOISE.getMechanismType()).isEqualTo(LAPLACE);
  }
}
