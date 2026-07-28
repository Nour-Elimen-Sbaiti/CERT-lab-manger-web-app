package com.cert.calibration_hub.equipement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FicheDeVieService {

    private final EquipementRepository equipementRepository;
    private final ContratMaintenanceRepository contratMaintenanceRepository;
    private final InterventionRepository interventionRepository;
    private final EtalonnageRepository etalonnageRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String LOGO_PATH = "static/logo_cert.png";

    private static final Font F_LABEL = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
    private static final Font F_VALEUR = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private static final Font F_SECTION = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private static final Font F_TITRE = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
    private static final Font F_PETIT = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
    private static final BaseColor GRIS_CLAIR = new BaseColor(235, 235, 235);

    public FicheDeVieService(EquipementRepository equipementRepository,
                             ContratMaintenanceRepository contratMaintenanceRepository,
                             InterventionRepository interventionRepository,
                             EtalonnageRepository etalonnageRepository) {
        this.equipementRepository = equipementRepository;
        this.contratMaintenanceRepository = contratMaintenanceRepository;
        this.interventionRepository = interventionRepository;
        this.etalonnageRepository = etalonnageRepository;
    }

    public byte[] genererPdf(Long equipementId) {
        Equipement e = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new EquipementNotFoundException(equipementId));

        List<ContratMaintenance> contrats = contratMaintenanceRepository
                .findByEquipementIdOrderByDateFinDesc(equipementId);
        List<Intervention> interventions = interventionRepository
                .findByEquipementIdOrderByDateInterventionDesc(equipementId);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A4, 30, 30, 20, 20);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            ajouterPage1(doc, e, contrats.isEmpty() ? null : contrats.get(0));
            doc.newPage();
            ajouterPage2(doc, e, interventions);

            doc.close();
            return baos.toByteArray();

        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la génération du PDF : " + ex.getMessage(), ex);
        }
    }

    // ================= PAGE 1 =================
    private void ajouterPage1(com.itextpdf.text.Document doc, Equipement e, ContratMaintenance contrat) throws Exception {
        ajouterEntete(doc, "FICHE DE VIE ÉQUIPEMENT", "Page : 1 / 2");

        PdfPTable cadre = new PdfPTable(2);
        cadre.setWidthPercentage(100);
        cadre.setWidths(new float[]{1f, 1f});

        cadre.addCell(sectionTitre("MATÉRIEL", 2));

        cadre.addCell(blocChamps(new String[][]{
                {"Désignation :", e.getDesignation()},
                {"Accessoire(s) :", "-"}
        }));
        cadre.addCell(blocChamps(new String[][]{
                {"Code d'identification :", e.getCodeIdentifiant()},
                {"N° d'immobilisation / propriétaire :", vide(e.getCodeEmplacement())}
        }));

        cadre.addCell(blocChamps(new String[][]{
                {"Marque :", e.getMarque()},
                {"Type / modèle :", e.getModele()},
                {"N° de série :", e.getNumeroSerie()}
        }));
        cadre.addCell(blocChamps(new String[][]{
                {"Fournisseur :", vide(e.getFournisseur())}
        }));

        cadre.addCell(blocChamps(new String[][]{
                {"Date de réception :", formater(e.getDateAcquisition())},
                {"Date de mise en service :", formater(e.getDateMiseEnService())},
                {"Garantie jusqu'au :", formater(e.getGarantieJusquAu())},
                {"Date de réforme :", formater(e.getDateReforme())}
        }));
        cadre.addCell(blocChamps(new String[][]{
                {"Contact technique :", ""},
                {"Nom :", "-"},
                {"Tél :", "-"},
                {"Fax :", "-"},
                {"Adresse :", "-"}
        }));

        String coche = contrat != null ? "[X] oui     [ ] non" : "[ ] oui     [X] non";
        PdfPTable contratBloc = new PdfPTable(1);
        contratBloc.addCell(ligneSimple("Contrat d'entretien :  " + coche));
        contratBloc.addCell(ligneSimple("Société :  " + (contrat != null ? contrat.getPrestataire() : "-")
                + "        N° du contrat :  -"));
        contratBloc.addCell(ligneSimple("Période d'effet :  " +
                (contrat != null ? formater(contrat.getDateDebut()) + " au " + formater(contrat.getDateFin()) : "-")));
        contratBloc.addCell(ligneSimple("Conditions du contrat :  " +
                (contrat != null ? contrat.getPeriodeValeur() + " " + contrat.getPeriodeUnite() + " — "
                        + (contrat.estValide() ? "Valide" : "Expiré") : "-")));
        PdfPCell contratCell = new PdfPCell(contratBloc);
        contratCell.setColspan(2);
        contratCell.setPadding(0);
        cadre.addCell(contratCell);

        cadre.addCell(sectionTitre("RESPONSABLE", 2));
        cadre.addCell(tripleChamp("Nom :", "Prénom :", "N° de poste :"));

        cadre.addCell(sectionTitre("SUPPLÉANT", 2));
        cadre.addCell(tripleChamp("Nom :", "Prénom :", "N° de poste :"));

        cadre.addCell(sectionTitre("OBSERVATIONS", 2));
        PdfPCell obsCell = new PdfPCell(new Phrase(vide(e.getObservations()), F_VALEUR));
        obsCell.setColspan(2);
        obsCell.setMinimumHeight(60f);
        obsCell.setPadding(6f);
        cadre.addCell(obsCell);

        doc.add(cadre);
    }

    // ================= PAGE 2 =================
    private void ajouterPage2(com.itextpdf.text.Document doc, Equipement e, List<Intervention> interventions) throws Exception {
        ajouterEntete(doc, "FICHE DE VIE MATÉRIEL", "Page : 2 / 2");

        PdfPTable refTable = new PdfPTable(2);
        refTable.setWidthPercentage(100);
        refTable.addCell(ligneChamp("Désignation :", e.getDesignation()));
        refTable.addCell(ligneChamp("Code d'identification :", e.getCodeIdentifiant()));
        doc.add(refTable);

        PdfPTable table = new PdfPTable(new float[]{1.6f, 4f, 1.3f, 1.3f, 1.6f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(4f);

        ajouterEnteteColonne(table, "Date");
        ajouterEnteteColonne(table, "Nature de l'intervention ou Observations");
        ajouterEnteteColonne(table, "Coût de l'intervention");
        ajouterEnteteColonne(table, "N° bon d'intervention");
        ajouterEnteteColonne(table, "Nom");

        for (Intervention i : interventions) {
            table.addCell(celluleCentree(formater(i.getDateIntervention())));
            table.addCell(celluleTexte(i.getType() + " — " + i.getNatureIntervention()));
            table.addCell(celluleCentree(i.getCout() != null ? i.getCout().toString() : "-"));
            table.addCell(celluleCentree(vide(i.getNumeroBonIntervention())));
            table.addCell(celluleTexte(vide(i.getIntervenantNom())
                    + (i.getIntervenantSociete() != null ? " (" + i.getIntervenantSociete() + ")" : "")));
        }

        int lignesVides = Math.max(0, 6 - interventions.size());
        for (int k = 0; k < lignesVides; k++) {
            for (int col = 0; col < 5; col++) {
                PdfPCell c = new PdfPCell(new Phrase(" "));
                c.setMinimumHeight(22f);
                table.addCell(c);
            }
        }

        doc.add(table);
    }

    // ================= EN-TÊTE COMMUN =================
    private void ajouterEntete(com.itextpdf.text.Document doc, String titre, String page) throws Exception {
        PdfPTable entete = new PdfPTable(new float[]{1f, 3f, 1f});
        entete.setWidthPercentage(100);

        PdfPCell logoCell;
        try {
            Image logo = Image.getInstance(getClass().getClassLoader().getResource(LOGO_PATH));
            logo.scaleToFit(60, 60);
            PdfPTable logoTable = new PdfPTable(1);
            PdfPCell imgCell = new PdfPCell(logo, false);
            imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imgCell.setBorder(Rectangle.NO_BORDER);
            logoTable.addCell(imgCell);
            logoCell = new PdfPCell(logoTable);
        } catch (Exception ex) {
            logoCell = new PdfPCell(new Phrase("Logo\nLaboratoire", F_PETIT));
        }
        logoCell.setBorder(Rectangle.BOX);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        entete.addCell(logoCell);

        PdfPTable titreTable = new PdfPTable(1);
        PdfPCell formulaireCell = new PdfPCell(new Phrase("Formulaire", F_PETIT));
        formulaireCell.setBorder(Rectangle.NO_BORDER);
        formulaireCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titreTable.addCell(formulaireCell);

        PdfPCell titrePrincipalCell = new PdfPCell(new Phrase(titre, F_TITRE));
        titrePrincipalCell.setBorder(Rectangle.NO_BORDER);
        titrePrincipalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titrePrincipalCell.setPaddingTop(4f);
        titreTable.addCell(titrePrincipalCell);

        PdfPCell titreCell = new PdfPCell(titreTable);
        titreCell.setBorder(Rectangle.BOX);
        titreCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        entete.addCell(titreCell);

        PdfPCell pageCell = new PdfPCell(new Phrase("Date :\n" + page, F_PETIT));
        pageCell.setBorder(Rectangle.BOX);
        pageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        entete.addCell(pageCell);

        entete.setSpacingAfter(6f);
        doc.add(entete);
    }

    // ================= UTILITAIRES DE MISE EN PAGE =================

    private PdfPCell sectionTitre(String texte, int colspan) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_SECTION));
        c.setColspan(colspan);
        c.setBackgroundColor(GRIS_CLAIR);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(4f);
        return c;
    }

    private PdfPCell blocChamps(String[][] champs) {
        PdfPTable t = new PdfPTable(1);
        for (String[] champ : champs) {
            String label = champ[0];
            String valeur = champ[1];
            Paragraph p = new Paragraph();
            p.add(new com.itextpdf.text.Chunk(label + " ", F_LABEL));
            if (!valeur.isBlank()) {
                p.add(new com.itextpdf.text.Chunk(valeur, F_VALEUR));
            }
            p.setSpacingAfter(3f);
            PdfPCell c = new PdfPCell(p);
            c.setBorder(Rectangle.NO_BORDER);
            c.setPadding(2f);
            t.addCell(c);
        }
        PdfPCell cell = new PdfPCell(t);
        cell.setPadding(4f);
        cell.setMinimumHeight(50f);
        return cell;
    }

    private PdfPCell ligneSimple(String texte) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_VALEUR));
        c.setPadding(4f);
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    private PdfPCell ligneChamp(String label, String valeur) {
        Paragraph p = new Paragraph();
        p.add(new com.itextpdf.text.Chunk(label + " ", F_LABEL));
        p.add(new com.itextpdf.text.Chunk(vide(valeur), F_VALEUR));
        PdfPCell c = new PdfPCell(p);
        c.setPadding(5f);
        return c;
    }

    private PdfPCell tripleChamp(String label1, String label2, String label3) {
        PdfPTable t = new PdfPTable(3);
        t.addCell(ligneChamp(label1, "-"));
        t.addCell(ligneChamp(label2, "-"));
        t.addCell(ligneChamp(label3, "-"));
        PdfPCell cell = new PdfPCell(t);
        cell.setColspan(2);
        cell.setPadding(0);
        return cell;
    }

    private void ajouterEnteteColonne(PdfPTable table, String texte) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_LABEL));
        c.setBackgroundColor(GRIS_CLAIR);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(5f);
        table.addCell(c);
    }

    private PdfPCell celluleTexte(String texte) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_VALEUR));
        c.setPadding(4f);
        return c;
    }

    private PdfPCell celluleCentree(String texte) {
        PdfPCell c = new PdfPCell(new Phrase(texte, F_VALEUR));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(4f);
        return c;
    }

    private String formater(java.time.LocalDate date) {
        return date != null ? date.format(FMT) : "-";
    }

    private String vide(String valeur) {
        return (valeur == null || valeur.isBlank()) ? "-" : valeur;
    }
}