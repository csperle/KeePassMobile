/*
    Copyright (c) 2009-2010 Christoph Sperle <keepassmobile@gmail.com>
    
    This file is part of KeePassMobile.

    KeePassMobile is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    KeePassMobile is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with KeePassMobile.  If not, see <http://www.gnu.org/licenses/>.

*/

package org.sperle.keepass.ui.util;

import java.util.Vector;

/**
 * Sorts a vectors of objects that implement the org.sperle.keepass.util.Comparable (!) interface using the
 * Quicksort algorithm.
 */
public class QuickSorter {

    /**
     * Performs the sort.
     * 
     * @param v vector of comparable objects
     */
    public static void sort(Vector v) {
        quickSort(v, 0, v.size() - 1);
        insertionSort(v);
    }

    private static void swap(Vector vect, int i, int j) {
        org.sperle.keepass.util.Comparable x = (org.sperle.keepass.util.Comparable) vect.elementAt(i);
        vect.setElementAt(vect.elementAt(j), i);
        vect.setElementAt(x, j);
    }

    private static void quickSort(Vector vect, int el, int yu) {
        if (yu - el < 16) {
            return;
        }
        int m, pivot, other;
        m = (yu + el) / 2;
        if (((org.sperle.keepass.util.Comparable) vect.elementAt(el)).compareTo(vect.elementAt(yu)) < 0) {
            pivot = el;
            other = yu;
        } else {
            pivot = yu;
            other = el;
        }
        if (((org.sperle.keepass.util.Comparable) vect.elementAt(pivot)).compareTo(vect.elementAt(m)) < 0) {
            if (((org.sperle.keepass.util.Comparable) vect.elementAt(m)).compareTo(vect.elementAt(other)) < 0) {
                pivot = m;
            } else {
                pivot = other;
            }
        }

        swap(vect, el, pivot);

        int i, j;
        i = el + 1;
        j = yu - 1;
        while (true) {
            while (((org.sperle.keepass.util.Comparable) vect.elementAt(el)).compareTo(vect.elementAt(i)) < 0) {
                i++;
            }
            while (((org.sperle.keepass.util.Comparable) vect.elementAt(el)).compareTo(vect.elementAt(j)) > 0) {
                j--;
            }
            if (i >= j) {
                break;
            }
            swap(vect, i, j);
            i++;
            j--;
        }
        swap(vect, el, j);
        if (j - el < yu - i) {
            quickSort(vect, el, j - 1);
            quickSort(vect, i, yu);
        } else {
            quickSort(vect, i, yu);
            quickSort(vect, el, j - 1);
        }
    }

    private static void insertionSort(Vector vect) {
        int i, j;
        for (i = 1; i < vect.size(); i++) {
            org.sperle.keepass.util.Comparable x = (org.sperle.keepass.util.Comparable) vect.elementAt(i);
            for (j = i; (j > 0) && ((org.sperle.keepass.util.Comparable) vect.elementAt(j - 1)).compareTo(x) > 0; j--) {
                vect.setElementAt(vect.elementAt(j - 1), j);
            }
            vect.setElementAt(x, j);
        }
    }
}
