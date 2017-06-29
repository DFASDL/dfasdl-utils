/*
 * Copyright (C) 2014 - 2017  Contributors as noted in the AUTHORS.md file
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dfasdl.utils.benchmarks

import org.dfasdl.utils.DocumentHelpers
import org.dfasdl.utils.ElementType.ElementType
import org.openjdk.jmh.annotations._
import org.w3c.dom.Element

@State(Scope.Thread)
class ElementHelpersBenchmark extends DocumentHelpers {

  @Benchmark
  def testGetElementTypeForDataElement: ElementType = {
    getElementType("formatnum")
  }

  @Benchmark
  def testGetElementTypeForExpressionElement: ElementType = {
    getElementType("const")
  }

  @Benchmark
  def testGetElementTypeForStructureElement: ElementType = {
    getElementType("elem")
  }

  @Benchmark
  def testGetElementTypeForRootElement: ElementType = {
    getElementType("dfasdl")
  }

  @Benchmark
  def testGetElementTypeForUnknownElement: ElementType = {
    getElementType("ThisTagNameMustBeUnknown")
  }

  val smallXml =
    """
      |<dfasdl xmlns="http://www.dfasdl.org/DFASDL" semantic="custom">
      |  <seq id="rows">
      |    <elem id="row">
      |      <str id="column-1" stop-sign=","/>
      |      <num id="column-2" stop-sign=","/>
      |      <str id="column-3"/>
      |    </elem>
      |  </seq>
      |</dfasdl>
    """.stripMargin
  val smallXmlTree = createNormalizedDocument(smallXml, useSchema = false)
  val smallXmlElement = smallXmlTree.getElementById("column-2")

  @Benchmark
  def testGetParentSequenceSmall: Option[Element] = {
    getParentSequence(smallXmlElement)
  }

  val mediumXml =
    """
      |<dfasdl xmlns="http://www.dfasdl.org/DFASDL" semantic="custom">
      |  <seq id="reservations">
      |    <elem id="reservation">
      |      <str id="buchungsnr"/>
      |      <str id="art"/>
      |      <str id="anreise"/>
      |      <str id="abreise"/>
      |      <str id="preis"/>
      |      <str id="preisaufschlag"/>
      |      <str id="waehrung"/>
      |      <str id="ankunftszeit"/>
      |      <str id="provision"/>
      |      <str id="mwst"/>
      |      <str id="status"/>
      |      <num id="erwachsene"/>
      |      <str id="anzahlung"/>
      |      <str id="anzahlung_datum"/>
      |      <str id="mwst_prov"/>
      |      <str id="bemerkungen"/>
      |      <str id="split_prov"/>
      |      <str id="buchungszeitpunkt"/>
      |      <str id="leistungen_vor_ort"/>
      |      <str id="kunde_nr"/>
      |      <num id="room_id"/>
      |      <str id="verm_gebuehr"/>
      |      <num id="vermittler_id"/>
      |      <seq id="kinder">
      |        <elem id="alter">
      |          <num id="kinder-alter-age" xml-attribute-name="age" xml-attribute-parent="alter"/>
      |          <num id="kinder-alter-anzahl" xml-attribute-name="anzahl" xml-attribute-parent="alter"/>
      |          <ref id="kinder-buchungsnr" sid="buchungsnr"/>
      |        </elem>
      |      </seq>
      |      <seq id="zusaetze">
      |        <elem id="zusatz">
      |          <str id="zusatz-name" xml-attribute-name="name" xml-attribute-parent="zusatz"/>
      |          <num id="zusatz-anzahl" xml-attribute-name="anzahl" xml-attribute-parent="zusatz"/>
      |          <str id="zusatz-einzelpreis" xml-attribute-name="einzelpreis" xml-attribute-parent="zusatz"/>
      |          <str id="zusatz-buchungsnr" xml-attribute-name="buchungsnr" xml-attribute-parent="zusatz"/>
      |          <str id="zusatz-provision" xml-attribute-name="provision" xml-attribute-parent="zusatz"/>
      |          <str id="zusatz-provision-split" xml-attribute-name="provision_split" xml-attribute-parent="zusatz"/>
      |          <str id="zusatz-mwst-satz" xml-attribute-name="mwst_satz" xml-attribute-parent="zusatz"/>
      |          <str id="zusatz-kuerzel" xml-attribute-name="kuerzel" xml-attribute-parent="zusatz"/>
      |        </elem>
      |      </seq>
      |      <str id="v_titel"/>
      |      <str id="v_plz"/>
      |      <str id="v_ort"/>
      |      <str id="v_strasse"/>
      |      <str id="v_telefon"/>
      |      <str id="v_fax"/>
      |      <str id="v_email"/>
      |      <str id="v_url"/>
      |      <str id="o_id"/>
      |      <str id="o_bezeichnung"/>
      |      <str id="o_plz"/>
      |      <str id="o_ort"/>
      |      <str id="o_strasse"/>
      |      <str id="o_url"/>
      |      <str id="o_telefon"/>
      |      <str id="o_fax"/>
      |      <str id="o_email"/>
      |      <str id="o_gewerblich"/>
      |      <str id="k_bezeichnung"/>
      |      <str id="z_bezeichnung"/>
      |      <str id="restzahlung_datum"/>
      |      <str id="systemgebuehr"/>
      |      <str id="systemgebuehr_waehrung"/>
      |      <str id="mietpreis"/>
      |      <str id="zm_anzahlung"/>
      |      <str id="zm_restzahlung"/>
      |      <str id="zm_zwischenzahlung"/>
      |      <str id="zm_mietzahlung"/>
      |      <str id="zm_stornogebuehr"/>
      |      <str id="ku_name"/>
      |      <str id="ku_vorname"/>
      |      <str id="ku_anrede"/>
      |      <str id="ku_land"/>
      |      <str id="ku_plz"/>
      |      <str id="ku_ort"/>
      |      <str id="ku_strasse"/>
      |      <str id="ku_telefon"/>
      |      <str id="ku_fax"/>
      |      <str id="ku_email"/>
      |      <str id="ku_handy"/>
      |      <str id="ku_firma"/>
      |      <num id="k_id"/>
      |    </elem>
      |  </seq>
      |</dfasdl>
    """.stripMargin
  val mediumXmlTree = createNormalizedDocument(mediumXml, useSchema = false)
  val mediumXmlElement = mediumXmlTree.getElementById("zusatz-provision")
  
  @Benchmark
  def testGetParentSequenceMedium: Option[Element] = {
    getParentSequence(mediumXmlElement)
  }

  val largeXml =
    """
      |<dfasdl xmlns="http://www.dfasdl.org/DFASDL" semantic="custom">
      |  <seq id="drupal_comment">
      |    <elem id="drupal_comment_row">
      |      <num db-column-name="cid" id="drupal_comment_row_cid" max-digits="11"/>
      |      <num db-column-name="pid" defaultnum="0" id="drupal_comment_row_pid" max-digits="11"/>
      |      <num db-column-name="nid" defaultnum="0" id="drupal_comment_row_nid" max-digits="11"/>
      |      <num db-column-name="uid" defaultnum="0" id="drupal_comment_row_uid" max-digits="11"/>
      |      <str db-column-name="subject" id="drupal_comment_row_subject" max-length="64"/>
      |      <str db-column-name="hostname" id="drupal_comment_row_hostname" max-length="128"/>
      |      <num db-column-name="created" defaultnum="0" id="drupal_comment_row_created" max-digits="11"/>
      |      <num db-column-name="changed" defaultnum="0" id="drupal_comment_row_changed" max-digits="11"/>
      |      <num db-column-name="status" defaultnum="1" id="drupal_comment_row_status" max-digits="3"/>
      |      <str db-column-name="thread" id="drupal_comment_row_thread" max-length="255"/>
      |      <str db-column-name="name" id="drupal_comment_row_name" max-length="60"/>
      |      <str db-column-name="mail" id="drupal_comment_row_mail" max-length="64"/>
      |      <str db-column-name="homepage" id="drupal_comment_row_homepage" max-length="255"/>
      |      <str db-column-name="language" id="drupal_comment_row_language" max-length="12"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_field_data_body">
      |    <elem id="drupal_field_data_body_row">
      |      <str db-column-name="entity_type" id="drupal_field_data_body_row_entity_type" max-length="128"/>
      |      <str db-column-name="bundle" id="drupal_field_data_body_row_bundle" max-length="128"/>
      |      <num db-column-name="deleted" defaultnum="0" id="drupal_field_data_body_row_deleted" max-digits="4"/>
      |      <num db-column-name="entity_id" id="drupal_field_data_body_row_entity_id" max-digits="10"/>
      |      <num db-column-name="revision_id" id="drupal_field_data_body_row_revision_id" max-digits="10"/>
      |      <str db-column-name="language" id="drupal_field_data_body_row_language" max-length="32"/>
      |      <num db-column-name="delta" id="drupal_field_data_body_row_delta" max-digits="10"/>
      |      <str db-column-name="body_value" id="drupal_field_data_body_row_body_value"/>
      |      <str db-column-name="body_summary" id="drupal_field_data_body_row_body_summary"/>
      |      <str db-column-name="body_format" id="drupal_field_data_body_row_body_format" max-length="255"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_field_data_comment_body">
      |    <elem id="drupal_field_data_comment_body_row">
      |      <str db-column-name="entity_type" id="drupal_field_data_comment_body_row_entity_type" max-length="128"/>
      |      <str db-column-name="bundle" id="drupal_field_data_comment_body_row_bundle" max-length="128"/>
      |      <num db-column-name="deleted" defaultnum="0" id="drupal_field_data_comment_body_row_deleted" max-digits="4"/>
      |      <num db-column-name="entity_id" id="drupal_field_data_comment_body_row_entity_id" max-digits="10"/>
      |      <num db-column-name="revision_id" id="drupal_field_data_comment_body_row_revision_id" max-digits="10"/>
      |      <str db-column-name="language" id="drupal_field_data_comment_body_row_language" max-length="32"/>
      |      <num db-column-name="delta" id="drupal_field_data_comment_body_row_delta" max-digits="10"/>
      |      <str db-column-name="comment_body_value" id="drupal_field_data_comment_body_row_comment_body_value"/>
      |      <str db-column-name="comment_body_format" id="drupal_field_data_comment_body_row_comment_body_format"
      |           max-length="255"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_field_data_field_tags">
      |    <elem id="drupal_field_data_field_tags_row">
      |      <str db-column-name="entity_type" id="drupal_field_data_field_tags_row_entity_type" max-length="128"/>
      |      <str db-column-name="bundle" id="drupal_field_data_field_tags_row_bundle" max-length="128"/>
      |      <num db-column-name="deleted" defaultnum="0" id="drupal_field_data_field_tags_row_deleted" max-digits="4"/>
      |      <num db-column-name="entity_id" id="drupal_field_data_field_tags_row_entity_id" max-digits="10"/>
      |      <num db-column-name="revision_id" id="drupal_field_data_field_tags_row_revision_id" max-digits="10"/>
      |      <str db-column-name="language" id="drupal_field_data_field_tags_row_language" max-length="32"/>
      |      <num db-column-name="delta" id="drupal_field_data_field_tags_row_delta" max-digits="10"/>
      |      <num db-column-name="field_tags_tid" id="drupal_field_data_field_tags_row_field_tags_tid" max-digits="10"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_field_revision_field_tags">
      |    <elem id="drupal_field_revision_field_tags_row">
      |      <str db-column-name="entity_type" id="drupal_field_revision_field_tags_row_entity_type" max-length="128"/>
      |      <str db-column-name="bundle" id="drupal_field_revision_field_tags_row_bundle" max-length="128"/>
      |      <num db-column-name="deleted" defaultnum="0" id="drupal_field_revision_field_tags_row_deleted" max-digits="4"/>
      |      <num db-column-name="entity_id" id="drupal_field_revision_field_tags_row_entity_id" max-digits="10"/>
      |      <num db-column-name="revision_id" id="drupal_field_revision_field_tags_row_revision_id" max-digits="10"/>
      |      <str db-column-name="language" id="drupal_field_revision_field_tags_row_language" max-length="32"/>
      |      <num db-column-name="delta" id="drupal_field_revision_field_tags_row_delta" max-digits="10"/>
      |      <num db-column-name="field_tags_tid" id="drupal_field_revision_field_tags_row_field_tags_tid" max-digits="10"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_node_comment_statistics">
      |    <elem id="drupal_node_comment_statistics_row">
      |      <num db-column-name="nid" defaultnum="0" id="drupal_node_comment_statistics_row_nid" max-digits="10"/>
      |      <num db-column-name="cid" defaultnum="0" id="drupal_node_comment_statistics_row_cid" max-digits="11"/>
      |      <num db-column-name="last_comment_timestamp" defaultnum="0"
      |           id="drupal_node_comment_statistics_row_last_comment_timestamp" max-digits="11"/>
      |      <str db-column-name="last_comment_name" id="drupal_node_comment_statistics_row_last_comment_name"
      |           max-length="60"/>
      |      <num db-column-name="last_comment_uid" defaultnum="0" id="drupal_node_comment_statistics_row_last_comment_uid"
      |           max-digits="11"/>
      |      <num db-column-name="comment_count" defaultnum="0" id="drupal_node_comment_statistics_row_comment_count"
      |           max-digits="10"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_node_revision">
      |    <elem id="drupal_node_revision_row">
      |      <num db-column-name="nid" defaultnum="0" id="drupal_node_revision_row_nid" max-digits="10"/>
      |      <num db-column-name="vid" id="drupal_node_revision_row_vid" max-digits="10"/>
      |      <num db-column-name="uid" defaultnum="0" id="drupal_node_revision_row_uid" max-digits="11"/>
      |      <str db-column-name="title" id="drupal_node_revision_row_title" max-length="255"/>
      |      <str db-column-name="log" id="drupal_node_revision_row_log"/>
      |      <num db-column-name="timestamp" defaultnum="0" id="drupal_node_revision_row_timestamp" max-digits="11"/>
      |      <num db-column-name="status" defaultnum="1" id="drupal_node_revision_row_status" max-digits="11"/>
      |      <num db-column-name="comment" defaultnum="0" id="drupal_node_revision_row_comment" max-digits="11"/>
      |      <num db-column-name="promote" defaultnum="0" id="drupal_node_revision_row_promote" max-digits="11"/>
      |      <num db-column-name="sticky" defaultnum="0" id="drupal_node_revision_row_sticky" max-digits="11"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_node">
      |    <elem id="drupal_node_row">
      |      <num db-column-name="nid" id="drupal_node_row_nid" max-digits="10"/>
      |      <num db-column-name="vid" id="drupal_node_row_vid" max-digits="10"/>
      |      <str db-column-name="type" id="drupal_node_row_type" max-length="32"/>
      |      <str db-column-name="language" id="drupal_node_row_language" max-length="12"/>
      |      <str db-column-name="title" id="drupal_node_row_title" max-length="255"/>
      |      <num db-column-name="uid" defaultnum="0" id="drupal_node_row_uid" max-digits="11"/>
      |      <num db-column-name="status" defaultnum="1" id="drupal_node_row_status" max-digits="11"/>
      |      <num db-column-name="created" defaultnum="0" id="drupal_node_row_created" max-digits="11"/>
      |      <num db-column-name="changed" defaultnum="0" id="drupal_node_row_changed" max-digits="11"/>
      |      <num db-column-name="comment" defaultnum="0" id="drupal_node_row_comment" max-digits="11"/>
      |      <num db-column-name="promote" defaultnum="0" id="drupal_node_row_promote" max-digits="11"/>
      |      <num db-column-name="sticky" defaultnum="0" id="drupal_node_row_sticky" max-digits="11"/>
      |      <num db-column-name="tnid" defaultnum="0" id="drupal_node_row_tnid" max-digits="10"/>
      |      <num db-column-name="translate" defaultnum="0" id="drupal_node_row_translate" max-digits="11"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_taxonomy_index">
      |    <elem id="drupal_taxonomy_index_row">
      |      <num db-column-name="nid" defaultnum="0" id="drupal_taxonomy_index_row_nid" max-digits="10"/>
      |      <num db-column-name="tid" defaultnum="0" id="drupal_taxonomy_index_row_tid" max-digits="10"/>
      |      <num db-column-name="sticky" defaultnum="0" id="drupal_taxonomy_index_row_sticky" max-digits="4"/>
      |      <num db-column-name="created" defaultnum="0" id="drupal_taxonomy_index_row_created" max-digits="11"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_taxonomy_term_data">
      |    <elem id="drupal_taxonomy_term_data_row">
      |      <num db-column-name="tid" id="drupal_taxonomy_term_data_row_tid" max-digits="10"/>
      |      <num db-column-name="vid" defaultnum="0" id="drupal_taxonomy_term_data_row_vid" max-digits="10"/>
      |      <str db-column-name="name" id="drupal_taxonomy_term_data_row_name" max-length="255"/>
      |      <str db-column-name="description" id="drupal_taxonomy_term_data_row_description"/>
      |      <str db-column-name="format" id="drupal_taxonomy_term_data_row_format" max-length="255"/>
      |      <num db-column-name="weight" defaultnum="0" id="drupal_taxonomy_term_data_row_weight" max-digits="11"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_taxonomy_term_hierarchy">
      |    <elem id="drupal_taxonomy_term_hierarchy_row">
      |      <num db-column-name="tid" defaultnum="0" id="drupal_taxonomy_term_hierarchy_row_tid" max-digits="10"/>
      |      <num db-column-name="parent" defaultnum="0" id="drupal_taxonomy_term_hierarchy_row_parent" max-digits="10"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_taxonomy_vocabulary">
      |    <elem id="drupal_taxonomy_vocabulary_row">
      |      <num db-column-name="vid" id="drupal_taxonomy_vocabulary_row_vid" max-digits="10"/>
      |      <str db-column-name="name" id="drupal_taxonomy_vocabulary_row_name" max-length="255"/>
      |      <str db-column-name="machine_name" id="drupal_taxonomy_vocabulary_row_machine_name" max-length="255"/>
      |      <str db-column-name="description" id="drupal_taxonomy_vocabulary_row_description"/>
      |      <num db-column-name="hierarchy" defaultnum="0" id="drupal_taxonomy_vocabulary_row_hierarchy" max-digits="3"/>
      |      <str db-column-name="module" id="drupal_taxonomy_vocabulary_row_module" max-length="255"/>
      |      <num db-column-name="weight" defaultnum="0" id="drupal_taxonomy_vocabulary_row_weight" max-digits="11"/>
      |    </elem>
      |  </seq>
      |
      |  <seq id="drupal_users">
      |    <elem id="drupal_users_row">
      |      <num db-column-name="uid" defaultnum="0" id="drupal_users_row_uid" max-digits="10"/>
      |      <str db-column-name="name" id="drupal_users_row_name" max-length="60"/>
      |      <str db-column-name="pass" id="drupal_users_row_pass" max-length="128"/>
      |      <str db-column-name="mail" id="drupal_users_row_mail" max-length="254"/>
      |      <str db-column-name="theme" id="drupal_users_row_theme" max-length="255"/>
      |      <str db-column-name="signature" id="drupal_users_row_signature" max-length="255"/>
      |      <str db-column-name="signature_format" id="drupal_users_row_signature_format" max-length="255"/>
      |      <num db-column-name="created" defaultnum="0" id="drupal_users_row_created" max-digits="11"/>
      |      <num db-column-name="access" defaultnum="0" id="drupal_users_row_access" max-digits="11"/>
      |      <num db-column-name="login" defaultnum="0" id="drupal_users_row_login" max-digits="11"/>
      |      <num db-column-name="status" defaultnum="0" id="drupal_users_row_status" max-digits="4"/>
      |      <str db-column-name="timezone" id="drupal_users_row_timezone" max-length="32"/>
      |      <str db-column-name="language" id="drupal_users_row_language" max-length="12"/>
      |      <num db-column-name="picture" defaultnum="0" id="drupal_users_row_picture" max-digits="11"/>
      |      <str db-column-name="init" id="drupal_users_row_init" max-length="254"/>
      |      <str db-column-name="data" id="drupal_users_row_data"/>
      |    </elem>
      |  </seq>
      |
      |</dfasdl>
    """.stripMargin
  val largeXmlTree = createNormalizedDocument(largeXml, useSchema = false)
  val largeXmlElement = largeXmlTree.getElementById("drupal_node_row_created")

  @Benchmark
  def testGetParentSequenceLarge: Option[Element] = {
    getParentSequence(largeXmlElement)
  }

}
