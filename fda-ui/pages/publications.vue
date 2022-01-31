<template>
  <div>
    <v-card flat>
      <v-card-title>FDA-DBRepo: A Data Preservation Repository Supporting FAIR Principles, Data Versioning and Reproducible Queries</v-card-title>
      <v-card-subtitle>
        Proceedings of the 17th International Conference on Digital Preservation
      </v-card-subtitle>
      <v-card-text>
        <h3>Authors</h3>
        <p>
          M. Weise, C. Michlits, M. Staudinger, G. Eva, K. Stytsenko, R. Ganguly, A. Rauber
        </p>
        <h3>Abstract</h3>
        <p>
          Database preservation frequently happens post-factum: databases are transferred and migrated into preservation formats and environments after a project has ended.
          This increases the risks concerning incompatibility and pushes the preservation burden after the initial lifetime and use of the data.
          We propose a database repository infrastructure, where databases are created, used and preserved directly in the data curation environment. This increases the FAIRness of the data curated as professional data stewardship activities accompany the databases right from the onset. We present the FAIR Data Austria Database Repository (FDA-DBRepo) infrastructure and provide a first version of an open-source reference implementation.
        </p>
        <h3>Venue</h3>
        <p>iPRES 2021, Beijing, China; 19.10.2021 - 21.10.2021</p>
        <v-btn
          color="primary"
          href="https://publik.tuwien.ac.at/files/publik_298296.pdf"
          target="_blank">
          View PDF
        </v-btn>
      </v-card-text>
    </v-card>
    <v-card class="mt-4" flat>
      <v-card-title>Better support for research: current cooperation projects</v-card-title>
      <v-card-subtitle>
        Newspaper article
      </v-card-subtitle>
      <v-card-text>
        <h3>Abstract</h3>
        <p>
          Currently 35 cooperation projects on digital and social transformation are being carried out at Austrian public universities, which are funded by the Federal Ministry of Education, Science and Research with 50 million euros. Among other things, the University of Vienna is involved in 2 projects of the Research Data Cluster in cooperation with the TU Vienna.
        </p>
        <v-btn
          color="primary"
          href="https://zid.univie.ac.at/it-news/artikel/news/cluster-forschungsdaten/"
          target="_blank">
          Read More
        </v-btn>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  components: {}
}
</script>
